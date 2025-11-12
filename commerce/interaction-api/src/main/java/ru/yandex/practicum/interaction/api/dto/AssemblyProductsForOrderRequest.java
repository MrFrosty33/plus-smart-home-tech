package ru.yandex.practicum.interaction.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AssemblyProductsForOrderRequest {
    private final Map<UUID, Integer> products;
    private final UUID orderId;
}
