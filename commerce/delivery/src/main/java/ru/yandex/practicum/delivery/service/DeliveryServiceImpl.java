package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.exception.NoDeliveryFoundException;
import ru.yandex.practicum.interaction.api.logging.Loggable;

import java.math.BigDecimal;
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

    @Override
    @Transactional
    @Loggable
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
                .orderId(deliveryDto.getOrderId())
                .build();

        addressRepository.saveAll(List.of(fromAddressEntity, toAddressEntity));
        deliveryRepository.save(delivery);

        return deliveryMapper.toDto(delivery);
    }

    @Override
    @Transactional
    @Loggable
    public void successful(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId).orElseThrow(() -> {
            log.warn("{}: no Deliveries found for orderId: {}", className, orderId);
            String message = "Deliveries for orderId: " + orderId + " cannot be found";
            String userMessage = "Deliveries not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new NoDeliveryFoundException(message, userMessage, status);
        });
        delivery.setDeliveryState(DeliveryState.DELIVERED);
    }

    @Override
    @Transactional
    @Loggable
    public void picked(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId).orElseThrow(() -> {
            log.warn("{}: no Deliveries found for orderId: {}", className, orderId);
            String message = "Deliveries for orderId: " + orderId + " cannot be found";
            String userMessage = "Deliveries not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new NoDeliveryFoundException(message, userMessage, status);
        });
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
    }

    @Override
    @Transactional
    @Loggable
    public void failed(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId).orElseThrow(() -> {
            log.warn("{}: no Deliveries found for orderId: {}", className, orderId);
            String message = "Deliveries for orderId: " + orderId + " cannot be found";
            String userMessage = "Deliveries not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new NoDeliveryFoundException(message, userMessage, status);
        });
        delivery.setDeliveryState(DeliveryState.FAILED);
    }

    @Override
    @Loggable
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        BigDecimal result = BigDecimal.valueOf(5);
        Delivery delivery = deliveryRepository.findByOrderId(orderDto.getOrderId()).orElseThrow(() -> {
            log.warn("{}: no Deliveries found for orderId: {}", className, orderDto.getOrderId());
            String message = "Deliveries for orderId: " + orderDto.getOrderId() + " cannot be found";
            String userMessage = "Deliveries not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new NoDeliveryFoundException(message, userMessage, status);
        });

        //todo буду пока в fromAddress везде вставлять значения, полученые со склада
        if (delivery.getFromAddress().getStreet().equals("ADDRESS_1")) {
            result = result.multiply(BigDecimal.valueOf(1));
        }

        if (delivery.getFromAddress().getStreet().equals("ADDRESS_2")) {
            result = result.multiply(BigDecimal.valueOf(2));
        }

        if (orderDto.isFragile()) {
            result = result.multiply(BigDecimal.valueOf(1.2));
        }

        result = result.add(orderDto.getDeliveryWeight().multiply(BigDecimal.valueOf(0.3)));

        result = result.add(orderDto.getDeliveryVolume().multiply(BigDecimal.valueOf(0.2)));

        if (!delivery.getToAddress().getStreet().equals(delivery.getFromAddress().getStreet())) {
            result = result.multiply(BigDecimal.valueOf(1.2));
        }

        return result;
    }
}
