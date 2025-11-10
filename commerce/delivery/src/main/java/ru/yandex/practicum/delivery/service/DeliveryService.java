package ru.yandex.practicum.delivery.service;

import ru.yandex.practicum.interaction.api.dto.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {
    DeliveryDto create(DeliveryDto deliveryDto);

    void successful(UUID orderId);

    void picked(UUID orderId);

    void failed(UUID orderId);

    BigDecimal calculateDeliveryCost(OrderDto orderDto);
}
