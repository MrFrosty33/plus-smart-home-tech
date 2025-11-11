package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.dto.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.OrderState;
import ru.yandex.practicum.interaction.api.dto.PaymentDto;
import ru.yandex.practicum.interaction.api.dto.ProductReturnRequest;
import ru.yandex.practicum.interaction.api.dto.ReturnProductsRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.exception.InternalServerException;
import ru.yandex.practicum.interaction.api.exception.NotFoundException;
import ru.yandex.practicum.interaction.api.feign.DeliveryFeignClient;
import ru.yandex.practicum.interaction.api.feign.PaymentFeignClient;
import ru.yandex.practicum.interaction.api.feign.ShoppingCartFeignClient;
import ru.yandex.practicum.interaction.api.feign.WarehouseFeignClient;
import ru.yandex.practicum.interaction.api.logging.Loggable;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.math.BigDecimal;
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
    private final DeliveryFeignClient deliveryFeignClient;
    private final PaymentFeignClient paymentFeignClient;

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
            throw new NotFoundException(message, userMessage, status);
        }

        return result;
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto create(CreateNewOrderRequest request) {
        Order order = Order.builder()
                .products(request.getShoppingCart().getProducts())
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .build();

        // сохраняем, чтобы получить orderId
        order = orderRepository.save(order);

        // сборка и бронь на складе
        BookedProductsDto bookedProducts = warehouseFeignClient
                .assemblyOrder(new AssemblyProductsForOrderRequest(order.getProducts(), order.getOrderId()));
        if (bookedProducts == null) {
            log.warn("{}: warehouseFeignClient is unavailable — assembly order request did not reach its destination.", className);
            String message = "Warehouse feignClient not available";
            throw new InternalServerException(message);
        }

        order.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        order.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        order.setFragile(bookedProducts.isFragile());

        // получаем адрес склада и создаём доставку
        DeliveryDto deliveryRequest = DeliveryDto.builder()
                .orderId(order.getOrderId())
                .toAddress(request.getDeliveryAddress())
                .fromAddress(warehouseFeignClient.getAddress())
                .build();

        DeliveryDto deliveryResult = deliveryFeignClient.createDelivery(deliveryRequest);
        if (deliveryResult == null) {
            log.warn("{}: deliveryFeignClient is unavailable — assembly order request did not reach its destination.", className);
            String message = "Delivery feignClient not available";
            throw new InternalServerException(message);
        }

        order.setDeliveryId(deliveryResult.getDeliveryId());
        order.setDeliveryPrice(deliveryFeignClient.calculateDeliveryCost(orderMapper.toDto(order)));

        // оплата
        order.setProductsPrice(paymentFeignClient.calculateProductCost(orderMapper.toDto(order)));
        order.setTotalPrice(paymentFeignClient.calculateTotalCost(orderMapper.toDto(order)));

        PaymentDto payment = paymentFeignClient.createPayment(orderMapper.toDto(order));
        if (payment == null) {
            log.warn("{}: paymentFeignClient is unavailable — assembly order request did not reach its destination.", className);
            String message = "Payment feignClient not available";
            throw new InternalServerException(message);
        }

        orderRepository.save(order);
        return orderMapper.toDto(order);
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
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = findInCacheOrDB(orderId);

        paymentFeignClient.paymentSuccess(orderId);

        order.setState(OrderState.PAID);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = findInCacheOrDB(orderId);

        paymentFeignClient.paymentFailed(orderId);

        order.setState(OrderState.PAYMENT_FAILED);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto delivery(UUID orderId) {
        Order order = findInCacheOrDB(orderId);

        deliveryFeignClient.deliverySuccessful(orderId);

        order.setState(OrderState.DELIVERED);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = findInCacheOrDB(orderId);

        deliveryFeignClient.deliveryFailed(orderId);

        order.setState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto completed(UUID orderId) {
        Order order = findInCacheOrDB(orderId);
        order.setState(OrderState.COMPLETED);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto calculateTotal(UUID orderId) {
        Order order = findInCacheOrDB(orderId);

        BigDecimal totalPrice = paymentFeignClient.calculateTotalCost(orderMapper.toDto(order));

        if (totalPrice == null) {
            log.warn("{}: paymentFeignClient is unavailable — calculate total price request did not reach its destination.", className);
            String message = "Payment feignClient not available";
            throw new InternalServerException(message);
        }

        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto calculateDelivery(UUID orderId) {
        Order order = findInCacheOrDB(orderId);

        BigDecimal deliveryPrice = deliveryFeignClient.calculateDeliveryCost(orderMapper.toDto(order));

        if (deliveryPrice == null) {
            log.warn("{}: deliveryFeignClient is unavailable — calculate delivery price request did not reach its destination.", className);
            String message = "Delivery feignClient not available";
            throw new InternalServerException(message);
        }

        order.setDeliveryPrice(deliveryPrice);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto calculateProduct(UUID orderId) {
        Order order = findInCacheOrDB(orderId);

        BigDecimal productPrice = paymentFeignClient.calculateProductCost(orderMapper.toDto(order));

        if (productPrice == null) {
            log.warn("{}: paymentFeignClient is unavailable — calculate total price request did not reach its destination.", className);
            String message = "Payment feignClient not available";
            throw new InternalServerException(message);
        }

        order.setProductsPrice(productPrice);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = findInCacheOrDB(orderId);
        BookedProductsDto bookedProducts = warehouseFeignClient.assemblyOrder(
                new AssemblyProductsForOrderRequest(order.getProducts(), orderId));

        order.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        order.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        order.setFragile(bookedProducts.isFragile());

        order.setState(OrderState.ASSEMBLED);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = findInCacheOrDB(orderId);

        order.setState(OrderState.ASSEMBLY_FAILED);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    private Order findInCacheOrDB(UUID orderId) {
        //todo cache
        return orderRepository.findById(orderId).orElseThrow(() -> {
            log.warn("{}: no Order found with orderId: {}", className, orderId);
            String message = "Order with orderId: " + orderId + " cannot be found";
            String userMessage = "Order not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new NotFoundException(message, userMessage, status);
        });
    }
}
