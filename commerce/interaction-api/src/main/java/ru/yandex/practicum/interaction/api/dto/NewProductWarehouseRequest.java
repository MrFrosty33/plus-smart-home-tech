package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NewProductWarehouseRequest {
    @Max(36)
    private final String productId;

    private final boolean fragile;

    private final DimensionDto dimension;

    @Min(1)
    private final double weight;
}
