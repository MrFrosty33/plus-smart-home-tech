package ru.yandex.practicum.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.dto.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.ProductReturnRequest;
import ru.yandex.practicum.interaction.api.feign.OrderFeignClient;
import ru.yandex.practicum.order.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Validated
public class OrderController implements OrderFeignClient {
    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getByUsername(@RequestParam String username) {
        return orderService.getByUsername(username);
    }

    @PutMapping
    public OrderDto createOrder(@RequestBody @Valid CreateNewOrderRequest request) {
        return orderService.create(request);
    }

    @PostMapping("/return")
    public OrderDto returnOrderRequest(ProductReturnRequest request) {
        return orderService.returnRequest(request);
    }

    @PostMapping("/payment")
    public OrderDto paymentOrder(@RequestBody UUID orderId) {
        return orderService.payment(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto paymentOrderFailed(@RequestBody UUID orderId) {
        return orderService.paymentFailed(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto deliveryOrder(@RequestBody UUID orderId) {
        return orderService.delivery(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto deliveryOrderFailed(@RequestBody UUID orderId) {
        return orderService.deliveryFailed(orderId);
    }

    @PostMapping("/completed")
    public OrderDto orderCompleted(@RequestBody UUID orderId) {
        return orderService.completed(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateOrderTotalPrice(@RequestBody UUID orderId) {
        return orderService.calculateTotal(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateOrderDeliveryPrice(@RequestBody UUID orderId) {
        return orderService.calculateDelivery(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assemblyOrder(@RequestBody UUID orderId) {
        return orderService.assembly(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyOrderFailed(@RequestBody UUID orderId) {
        return orderService.assemblyFailed(orderId);
    }
}
