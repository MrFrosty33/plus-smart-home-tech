package ru.yandex.practicum.shopping.store.model;

import lombok.Data;
import ru.yandex.practicum.interaction.api.dto.QuantityState;

@Data
public class SetProductQuantityStateRequest {
    private final String productId;
    private final QuantityState quantityState;
}
