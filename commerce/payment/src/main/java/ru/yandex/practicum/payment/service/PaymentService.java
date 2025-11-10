package ru.yandex.practicum.payment.service;

import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.PaymentDto;

public interface PaymentService {
    PaymentDto createPayment(OrderDto orderDto);
}
