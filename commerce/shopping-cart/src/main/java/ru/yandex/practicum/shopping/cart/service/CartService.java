package ru.yandex.practicum.shopping.cart.service;

import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.shopping.cart.model.ChangeProductQuantityRequest;

import java.util.Map;
import java.util.Set;

public interface CartService {
    ShoppingCartDto get(String username);

    ShoppingCartDto addProduct(String username, Map<String, Integer> products);

    void deactivateCart(String username);

    ShoppingCartDto removeProducts(String username, Set<String> productsId);

    ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request);
}
