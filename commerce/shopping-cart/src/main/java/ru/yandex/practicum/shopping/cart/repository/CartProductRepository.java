package ru.yandex.practicum.shopping.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.shopping.cart.model.CartProduct;
import ru.yandex.practicum.shopping.cart.model.CartProductEmbeddedId;

public interface CartProductRepository extends JpaRepository<CartProduct, CartProductEmbeddedId> {
}
