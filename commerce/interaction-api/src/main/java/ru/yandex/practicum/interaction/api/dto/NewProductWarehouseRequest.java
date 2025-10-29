package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class NewProductWarehouseRequest {
    @Length(max = 36)
    private final String productId;

    private final boolean fragile;

    private final DimensionDto dimension;

    @Min(1)
    private final BigDecimal weight;
}
