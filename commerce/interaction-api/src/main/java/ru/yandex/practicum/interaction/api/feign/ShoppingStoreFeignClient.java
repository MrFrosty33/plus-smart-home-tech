package ru.yandex.practicum.interaction.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.QuantityState;

import java.util.UUID;

@FeignClient(name = "shopping-store", fallback = ShoppingStoreFeignFallback.class)
public interface ShoppingStoreFeignClient {
    // пока только один маппинг используется, остальные буду добавлять по мере надобности
    @PostMapping("/api/v1/shopping-store/quantityState")
    ProductDto updateQuantityState(@RequestParam UUID productId, @RequestParam QuantityState quantityState);

    @GetMapping("/api/v1/shopping-store//{productId}")
    ProductDto getById(@PathVariable UUID productId);
}
