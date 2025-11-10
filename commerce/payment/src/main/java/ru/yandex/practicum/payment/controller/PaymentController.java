package ru.yandex.practicum.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.PaymentDto;
import ru.yandex.practicum.interaction.api.feign.PaymentFeignClient;
import ru.yandex.practicum.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Validated
public class PaymentController implements PaymentFeignClient {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto) {
        return paymentService.createPayment(orderDto);
    }

    @PostMapping("/totalCost")
    public BigDecimal calculateTotalCost(@RequestBody @Valid OrderDto orderDto) {
        return paymentService.calculateTotalCost(orderDto);
    }

    @PostMapping("/refund")
    public void refundPayment(@RequestBody UUID paymentId) {
        paymentService.refundPayment(paymentId);
    }

    @PostMapping("/productCost")
    public BigDecimal calculateProductCost(@RequestBody @Valid OrderDto orderDto) {
        return paymentService.calculateProductCost(orderDto);
    }

    @PostMapping("/failed")
    public void paymentFailed(@RequestBody UUID paymentId) {
        paymentService.paymentFailed(paymentId);
    }
}
