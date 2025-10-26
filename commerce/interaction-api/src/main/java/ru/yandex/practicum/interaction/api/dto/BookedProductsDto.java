package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BookedProductsDto {
    @Positive
    private final double deliveryWeight;

    @Positive
    private final double deliveryVolume;

    private final boolean fragile;
}
