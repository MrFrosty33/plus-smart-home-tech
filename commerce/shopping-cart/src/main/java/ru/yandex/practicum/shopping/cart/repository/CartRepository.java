package ru.yandex.practicum.shopping.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.shopping.cart.model.Cart;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUsernameAndActive(String username, boolean active);

    Optional<List<Cart>> findAllByUsernameAndActive(String username, boolean active);
}
