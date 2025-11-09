package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class OrderDto {
    private final UUID orderId;
    private final UUID shoppingCartId;

    private final Map<UUID, Integer> products;

    private final UUID paymentId;
    private final UUID deliveryId;

    @Positive
    private final BigDecimal deliveryWeight;
    @Positive
    private final BigDecimal deliveryVolume;

    private final boolean fragile;

    @Positive
    private final BigDecimal totalPrice;
    @Positive
    private final BigDecimal deliveryPrice;
    @Positive
    private final BigDecimal productPrice;
}
