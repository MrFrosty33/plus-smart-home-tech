package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DimensionDto {
    @Min(1)
    private final double width;
    @Min(1)
    private final double height;
    @Min(1)
    private final double depth;
}
