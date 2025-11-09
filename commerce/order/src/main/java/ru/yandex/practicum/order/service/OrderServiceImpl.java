package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.OrderState;
import ru.yandex.practicum.interaction.api.dto.ProductReturnRequest;
import ru.yandex.practicum.interaction.api.dto.ReturnProductsRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.exception.InternalServerException;
import ru.yandex.practicum.interaction.api.exception.NoOrderFoundException;
import ru.yandex.practicum.interaction.api.feign.ShoppingCartFeignClient;
import ru.yandex.practicum.interaction.api.feign.WarehouseFeignClient;
import ru.yandex.practicum.interaction.api.logging.Loggable;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final String className = this.getClass().getSimpleName();

    private final ShoppingCartFeignClient cartFeignClient;
    private final WarehouseFeignClient warehouseFeignClient;

    @Override
    @Loggable
    public List<OrderDto> getByUsername(String username) {
        // сейчас получается, что я в поиске заказов опираюсь на деактивированные корзины
        // моя логика, что при оформлении заказа корзина деактивируется и архивируется
        List<ShoppingCartDto> cartDto = cartFeignClient.getAllPastByUsername(username);
        if (cartDto == null) {
            log.warn("{}: shoppingCart is unavailable — get request did not reach its destination.", className);
            String message = "Shopping-cart feignClient not available";
            throw new InternalServerException(message);
        }

        List<OrderDto> result = new ArrayList<>();
        cartDto.forEach((dto) -> {
            Optional<Order> order = orderRepository.findByShoppingCartId(dto.getShoppingCartId());
            order.ifPresent(value -> result.add(orderMapper.toDto(value)));
        });

        if (result.isEmpty()) {
            log.warn("{}: no Orders found for username: {}", className, username);
            String message = "Orders for username: " + username + " cannot be found";
            String userMessage = "Orders not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            throw new NoOrderFoundException(message, userMessage, status);
        }

        return result;
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto create(CreateNewOrderRequest request) {
        //todo на момент создания заказа, он же ещё не собран, не оплачен и не отправлен? Эти поля пустуют же?
        // известно только количество товаров
        return null;
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto returnRequest(ProductReturnRequest request) {
        Order order = findInCacheOrDB(request.getOrderId());

        request.getProducts().forEach((key, value) -> {
            Map<UUID, Integer> orderProducts = order.getProducts();
            Integer oldValue = orderProducts.get(key);

            orderProducts.put(key, oldValue - value);
            if (orderProducts.get(key) <= 0) orderProducts.remove(key);
        });

        warehouseFeignClient.returnProducts(new ReturnProductsRequest(request.getProducts()));
        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto payment(UUID orderId) {
        return null;
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        //todo какова будет логика во время провала чего-либо?
        // Просто достаём текущее состояние заказа и возвращаем его?
        return null;
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto delivery(UUID orderId) {
        Order order = findInCacheOrDB(orderId);
        //todo обращаемся в delivery
        return null;
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        //todo delivery вызывает этот метод, если что-то пошло не так?
        Order order = findInCacheOrDB(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto completed(UUID orderId) {
        return null;
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto calculateTotal(UUID orderId) {
        return null;
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto calculateDelivery(UUID orderId) {
        return null;
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = findInCacheOrDB(orderId);
        BookedProductsDto bookedProducts = warehouseFeignClient.assembly(
                new AssemblyProductsForOrderRequest(order.getProducts(), orderId));

        order.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        order.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        order.setFragile(bookedProducts.isFragile());

        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        return null;
    }

    private Order findInCacheOrDB(UUID orderId) {
        //todo cache
        return orderRepository.findById(orderId).orElseThrow(() -> {
            log.warn("{}: no Order found with orderId: {}", className, orderId);
            String message = "Order with orderId: " + orderId + " cannot be found";
            String userMessage = "Order not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new NoOrderFoundException(message, userMessage, status);
        });
    }
}
