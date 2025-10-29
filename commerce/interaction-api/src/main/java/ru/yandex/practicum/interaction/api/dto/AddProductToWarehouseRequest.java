package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AddProductToWarehouseRequest {
    @NotNull
    private final UUID productId;

    @Min(1)
    private final int quantity;
}
