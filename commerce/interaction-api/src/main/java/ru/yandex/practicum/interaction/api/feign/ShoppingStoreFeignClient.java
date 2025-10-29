package ru.yandex.practicum.interaction.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.SetProductQuantityStateRequest;

@FeignClient(name = "shopping-store", fallback = ShoppingStoreFeignFallback.class)
public interface ShoppingStoreFeignClient {
    // пока только один маппинг используется, остальные буду добавлять по мере надобности
    @PostMapping("/api/v1/shopping-store/quantityState")
    ProductDto updateQuantityState(SetProductQuantityStateRequest request);
}
