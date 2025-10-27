package ru.yandex.practicum.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.warehouse.model.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
}
