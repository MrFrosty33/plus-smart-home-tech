package ru.yandex.practicum.interaction.api.feign;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.QuantityState;

import java.util.UUID;

@Component
public class ShoppingStoreFeignFallback implements ShoppingStoreFeignClient {
    @Override
    public ProductDto updateQuantityState(UUID productId, QuantityState quantityState) {
        return null;
    }
}
