package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class BookedProductsDto {
    @Positive
    private final BigDecimal deliveryWeight;

    @Positive
    private final BigDecimal deliveryVolume;

    private final boolean fragile;
}
