package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AddProductToWarehouseRequest {
    @Max(36)
    private final String productId;

    @Min(1)
    private final int quantity;
}
