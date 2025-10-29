package ru.yandex.practicum.interaction.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ShoppingCartDto {
    private final UUID shoppingCartId;

    private final Map<UUID, Integer> products;
}
