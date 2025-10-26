package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class DimensionDto {
    @Min(1)
    private final BigDecimal width;
    @Min(1)
    private final BigDecimal height;
    @Min(1)
    private final BigDecimal depth;
}
