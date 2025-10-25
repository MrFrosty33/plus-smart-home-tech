package ru.yandex.practicum.shopping.store.model;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.interaction.api.dto.QuantityState;

@Data
@AllArgsConstructor
public class SetProductQuantityStateRequest {
    @Max(36)
    private final String productId;
    private final QuantityState quantityState;
}
