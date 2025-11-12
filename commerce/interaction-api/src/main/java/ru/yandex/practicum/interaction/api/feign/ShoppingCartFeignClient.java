package ru.yandex.practicum.interaction.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

import java.util.List;

@FeignClient(name = "shopping-cart", fallback = ShoppingCartFeignFallback.class)
public interface ShoppingCartFeignClient {

    @GetMapping("/api/v1/shopping-cart/allPast")
    public List<ShoppingCartDto> getAllPastByUsername(@RequestParam String username);

    @DeleteMapping("/api/v1/shopping-cart")
    public void deactivateCart(@RequestParam String username);
}
