package ru.yandex.practicum.shopping.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.shopping.store.model.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
}
