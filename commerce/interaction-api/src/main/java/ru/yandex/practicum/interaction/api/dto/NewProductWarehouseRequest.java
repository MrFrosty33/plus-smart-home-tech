package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class NewProductWarehouseRequest {
    private final UUID productId;

    private final boolean fragile;

    private final DimensionDto dimension;

    @Min(1)
    private final BigDecimal weight;
}
