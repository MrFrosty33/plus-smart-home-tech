package ru.yandex.practicum.interaction.api.feign;

import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

import java.util.List;

public class ShoppingCartFeignFallback implements ShoppingCartFeignClient {
    @Override
    public List<ShoppingCartDto> getAllPastByUsername(String username) {
        return null;
    }

    @Override
    public void deactivateCart(String username) {

    }
}
