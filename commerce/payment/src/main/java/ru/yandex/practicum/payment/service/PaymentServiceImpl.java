package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.PaymentDto;
import ru.yandex.practicum.interaction.api.logging.Loggable;
import ru.yandex.practicum.payment.mapper.PaymentMapper;
import ru.yandex.practicum.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final String className = this.getClass().getSimpleName();
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;


    @Override
    @Transactional
    @Loggable
    public PaymentDto createPayment(OrderDto orderDto) {
        return null;
    }

    @Override
    @Loggable
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        return null;
    }

    @Override
    @Transactional
    @Loggable
    public void refundPayment(UUID paymentId) {

    }

    @Override
    @Loggable
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        return null;
    }

    @Override
    @Transactional
    @Loggable
    public void paymentSuccess(UUID paymentId) {

    }

    @Override
    @Transactional
    @Loggable
    public void paymentFailed(UUID paymentId) {

    }
}
