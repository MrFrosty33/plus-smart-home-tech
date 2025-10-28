package ru.yandex.practicum.interaction.api.feign;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.SetProductQuantityStateRequest;

@Component
public class ShoppingStoreFeignFallback implements ShoppingStoreFeignClient {
    @Override
    public ProductDto updateQuantityState(SetProductQuantityStateRequest request) {
        return null;
    }
}
