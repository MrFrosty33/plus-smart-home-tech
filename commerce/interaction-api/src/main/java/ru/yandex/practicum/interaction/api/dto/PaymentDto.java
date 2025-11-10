package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class PaymentDto {
    private final UUID paymentId;

    @Positive
    private final BigDecimal totalPayment;
    @Positive
    private final BigDecimal deliveryTotal;
    @Positive
    private final BigDecimal feeTotal;
}
