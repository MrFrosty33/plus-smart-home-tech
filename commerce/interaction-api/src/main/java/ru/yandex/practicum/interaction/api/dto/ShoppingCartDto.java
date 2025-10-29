package ru.yandex.practicum.interaction.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class ShoppingCartDto {
    @Length(max = 36)
    private final String shoppingCartId;

    private final Map<String, Integer> products;
}
