package ru.yandex.practicum.shopping.cart.service;

import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.shopping.cart.model.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CartService {
    ShoppingCartDto getByUsername(String username);

    List<ShoppingCartDto> getAllPastByUsername(String username);

    ShoppingCartDto addProduct(String username, Map<UUID, Integer> products);

    void deactivateCart(String username);

    ShoppingCartDto removeProducts(String username, Set<UUID> productsId);

    ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request);
}
