package ru.yandex.practicum.interaction.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class SetProductQuantityStateRequest {
    @Length(max = 36)
    private final String productId;
    private final QuantityState quantityState;
}
