package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@Builder
public class AddProductToWarehouseRequest {
    @Length(max = 36)
    private final String productId;

    @Min(1)
    private final int quantity;
}
