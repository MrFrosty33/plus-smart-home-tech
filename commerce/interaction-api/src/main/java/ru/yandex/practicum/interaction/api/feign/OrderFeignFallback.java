package ru.yandex.practicum.interaction.api.feign;

import ru.yandex.practicum.interaction.api.dto.OrderDto;

import java.util.UUID;

public class OrderFeignFallback implements OrderFeignClient {
    @Override
    public OrderDto paymentFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        return null;
    }
}
