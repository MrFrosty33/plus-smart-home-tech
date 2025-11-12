package ru.yandex.practicum.interaction.api.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "payment", fallback = PaymentFeignFallback.class)
public interface PaymentFeignClient {
    @PostMapping("/api/v1/payment")
    PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/api/v1/payment/totalCost")
    BigDecimal calculateTotalCost(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/api/v1/payment/productCost")
    BigDecimal calculateProductCost(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/api/v1/payment/success")
    void paymentSuccess(@RequestBody UUID paymentId);

    @PostMapping("/api/v1/payment/failed")
    void paymentFailed(@RequestBody UUID paymentId);
}
