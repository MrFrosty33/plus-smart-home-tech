package ru.yandex.practicum.shopping.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.shopping.store.model.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
    Page<Product> findAllByProductCategory(ProductCategory productCategory, Pageable pageable);

    long deleteByProductId(String productId);
}
