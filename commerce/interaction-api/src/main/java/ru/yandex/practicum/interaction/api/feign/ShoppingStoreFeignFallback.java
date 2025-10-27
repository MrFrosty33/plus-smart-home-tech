package ru.yandex.practicum.interaction.api.feign;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.api.dto.SetProductQuantityStateRequest;

@Component
public class ShoppingStoreFeignFallback implements ShoppingStoreFeignClient {
    @Override
    public boolean updateQuantityState(SetProductQuantityStateRequest request) {
        return false;
    }
}
