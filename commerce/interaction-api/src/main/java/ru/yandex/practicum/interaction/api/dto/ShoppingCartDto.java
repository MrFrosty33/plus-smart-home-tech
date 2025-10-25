package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class ShoppingCartDto {
    @Max(36)
    private final String shoppingCartId;

    private final Map<String, Integer> products;
}
