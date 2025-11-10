package ru.yandex.practicum.interaction.api.feign;

import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentFeignFallback implements PaymentFeignClient {
    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        return null;
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        return null;
    }

    @Override
    public void refundPayment(UUID paymentId) {

    }

    @Override
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        return null;
    }

    @Override
    public void paymentSuccess(UUID paymentId) {

    }

    @Override
    public void paymentFailed(UUID paymentId) {

    }
}
