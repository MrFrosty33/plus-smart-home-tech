package ru.yandex.practicum.interaction.api.feign;

import ru.yandex.practicum.interaction.api.dto.OrderDto;

import java.util.UUID;

public class OrderFeignFallback implements OrderFeignClient {

    @Override
    public OrderDto paymentOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto paymentOrderFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto deliveryOrderFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto assemblyOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto assemblyOrderFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto orderCompleted(UUID orderId) {
        return null;
    }
}
