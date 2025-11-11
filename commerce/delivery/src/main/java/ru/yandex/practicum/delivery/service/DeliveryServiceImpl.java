package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.mapper.AddressMapper;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.Address;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.repository.AddressRepository;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.DeliveryState;
import ru.yandex.practicum.interaction.api.dto.OrderBookingAddDeliveryRequest;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.exception.InternalServerException;
import ru.yandex.practicum.interaction.api.exception.NotFoundException;
import ru.yandex.practicum.interaction.api.feign.OrderFeignClient;
import ru.yandex.practicum.interaction.api.feign.WarehouseFeignClient;
import ru.yandex.practicum.interaction.api.logging.Loggable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {
    private final String className = this.getClass().getSimpleName();
    private final AddressRepository addressRepository;
    private final DeliveryRepository deliveryRepository;
    private final AddressMapper addressMapper;
    private final DeliveryMapper deliveryMapper;

    private final OrderFeignClient orderFeignClient;
    private final WarehouseFeignClient warehouseFeignClient;

    private final CacheManager cacheManager;

    @Override
    @Transactional
    @Loggable
    @CachePut(cacheNames = "delivery.deliveries", key = "#result.orderId")
    public DeliveryDto create(DeliveryDto deliveryDto) {
        AddressDto fromAddressDto = deliveryDto.getFromAddress();
        Address fromAddressEntity = addressRepository
                .findByCountryAndCityAndStreetAndHouseAndFlat(
                        fromAddressDto.getCountry(),
                        fromAddressDto.getCity(),
                        fromAddressDto.getStreet(),
                        fromAddressDto.getHouse(),
                        fromAddressDto.getFlat())
                .orElse(Address.builder()
                        .country(fromAddressDto.getCountry())
                        .city(fromAddressDto.getCity())
                        .street(fromAddressDto.getStreet())
                        .house(fromAddressDto.getHouse())
                        .flat(fromAddressDto.getFlat())
                        .build());

        AddressDto toAddressDto = deliveryDto.getFromAddress();
        Address toAddressEntity = addressRepository
                .findByCountryAndCityAndStreetAndHouseAndFlat(
                        toAddressDto.getCountry(),
                        toAddressDto.getCity(),
                        toAddressDto.getStreet(),
                        toAddressDto.getHouse(),
                        toAddressDto.getFlat())
                .orElse(Address.builder()
                        .country(toAddressDto.getCountry())
                        .city(toAddressDto.getCity())
                        .street(toAddressDto.getStreet())
                        .house(toAddressDto.getHouse())
                        .flat(toAddressDto.getFlat())
                        .build());

        Delivery delivery = Delivery.builder()
                .deliveryState(DeliveryState.CREATED)
                .orderId(deliveryDto.getOrderId())
                .fromAddress(fromAddressEntity)
                .toAddress(toAddressEntity)
                .build();

        addressRepository.saveAll(List.of(fromAddressEntity, toAddressEntity));
        deliveryRepository.save(delivery);

        return deliveryMapper.toDto(delivery);
    }

    @Override
    @Transactional
    @Loggable
    public void deliverySuccessful(UUID orderId) {
        Delivery delivery = findInCacheOrDbByOrderId(orderId);

        delivery.setDeliveryState(DeliveryState.DELIVERED);

        deliveryRepository.save(delivery);
        cacheManager.getCache("delivery.deliveries").put(orderId, deliveryMapper.toDto(delivery));

        log.info("{}: update delivery with id: {}, new status: {}",
                className, delivery.getDeliveryId(), delivery.getDeliveryState());

        OrderDto order = orderFeignClient.deliveryOrder(orderId);
        if (order == null) {
            log.warn("{}: orderFeignClient is unavailable — delivery failed request did not reach its destination.", className);
            String message = "Order feignClient not available";
            throw new InternalServerException(message);
        }
    }

    @Override
    @Transactional
    @Loggable
    public void deliveryPicked(UUID orderId) {
        Delivery delivery = findInCacheOrDbByOrderId(orderId);

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);

        delivery = deliveryRepository.save(delivery);
        cacheManager.getCache("delivery.deliveries").put(orderId, deliveryMapper.toDto(delivery));

        log.info("{}: update delivery with id: {}, new status: {}",
                className, delivery.getDeliveryId(), delivery.getDeliveryState());

        warehouseFeignClient.addDelivery(new OrderBookingAddDeliveryRequest(orderId, delivery.getDeliveryId()));
    }

    @Override
    @Transactional
    @Loggable
    public void deliveryFailed(UUID orderId) {
        Delivery delivery = findInCacheOrDbByOrderId(orderId);

        delivery.setDeliveryState(DeliveryState.FAILED);

        deliveryRepository.save(delivery);
        cacheManager.getCache("delivery.deliveries").put(orderId, deliveryMapper.toDto(delivery));

        log.info("{}: update delivery with id: {}, new status: {}",
                className, delivery.getDeliveryId(), delivery.getDeliveryState());

        OrderDto order = orderFeignClient.deliveryOrderFailed(orderId);
        if (order == null) {
            log.warn("{}: orderFeignClient is unavailable — delivery failed request did not reach its destination.", className);
            String message = "Order feignClient not available";
            throw new InternalServerException(message);
        }
    }

    @Override
    @Loggable
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        BigDecimal deliveryCostResult = BigDecimal.valueOf(5);
        deliveryCostResult = deliveryCostResult.setScale(2, RoundingMode.UP);

        log.info("{}: initial deliveryCost value: {}", className, deliveryCostResult);

        Delivery delivery = findInCacheOrDbByOrderId(orderDto.getOrderId());

        if (delivery.getFromAddress().getStreet().equals("ADDRESS_1")) {
            deliveryCostResult = deliveryCostResult.multiply(BigDecimal.valueOf(1));
        }

        if (delivery.getFromAddress().getStreet().equals("ADDRESS_2")) {
            deliveryCostResult = deliveryCostResult.multiply(BigDecimal.valueOf(2));
        }

        log.info("{}: deliveryCost value after initial address check: {}", className, deliveryCostResult);

        if (orderDto.isFragile()) {
            deliveryCostResult = deliveryCostResult.multiply(BigDecimal.valueOf(1.2));
            log.info("{}: deliveryCost value after fragile check: {}", className, deliveryCostResult);
        }

        deliveryCostResult = deliveryCostResult.add(orderDto.getDeliveryWeight().multiply(BigDecimal.valueOf(0.3)));
        log.info("{}: deliveryCost value after weight check: {}", className, deliveryCostResult);

        deliveryCostResult = deliveryCostResult.add(orderDto.getDeliveryVolume().multiply(BigDecimal.valueOf(0.2)));
        log.info("{}: deliveryCost value after volume check: {}", className, deliveryCostResult);

        if (!delivery.getToAddress().getStreet().equals(delivery.getFromAddress().getStreet())) {
            deliveryCostResult = deliveryCostResult.multiply(BigDecimal.valueOf(1.2));
            log.info("{}: deliveryCost value after destination address check: {}", className, deliveryCostResult);
        }

        return deliveryCostResult;
    }

    private Delivery findInCacheOrDbByOrderId(UUID orderId) {
        Cache.ValueWrapper valueWrapper = cacheManager.getCache("delivery.deliveries").get(orderId);
        Delivery delivery;

        if (valueWrapper != null) {
            delivery = deliveryMapper.toEntity((DeliveryDto) valueWrapper.get());
            log.info("{}: found Delivery in cache", className);
        } else {
            delivery = deliveryRepository.findByOrderId(orderId).orElseThrow(() -> {
                log.warn("{}: no Deliveries found for orderId: {}", className, orderId);
                String message = "Deliveries for orderId: " + orderId + " cannot be found";
                String userMessage = "Deliveries not found";
                HttpStatus status = HttpStatus.NOT_FOUND;
                return new NotFoundException(message, userMessage, status);
            });
            log.info("{}: found Delivery in DB", className);
        }

        return delivery;
    }
}
