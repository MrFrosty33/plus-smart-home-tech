package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SetProductQuantityStateRequest {
    @Max(36)
    private final String productId;
    private final QuantityState quantityState;
}
