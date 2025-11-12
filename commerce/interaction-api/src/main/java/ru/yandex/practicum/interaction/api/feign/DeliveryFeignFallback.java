package ru.yandex.practicum.interaction.api.feign;

import ru.yandex.practicum.interaction.api.dto.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public class DeliveryFeignFallback implements DeliveryFeignClient {
    @Override
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        return null;
    }

    @Override
    public void deliverySuccessful(UUID orderId) {

    }

    @Override
    public void deliveryPicked(UUID orderId) {

    }

    @Override
    public void deliveryFailed(UUID orderId) {

    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        return BigDecimal.ZERO;
    }
}
