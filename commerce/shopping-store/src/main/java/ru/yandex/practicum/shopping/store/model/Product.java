package ru.yandex.practicum.shopping.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.interaction.api.dto.ProductCategory;
import ru.yandex.practicum.interaction.api.dto.ProductState;
import ru.yandex.practicum.interaction.api.dto.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @Column(name = "product_id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 1000)
    private String productName;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "image_src", length = 1000)
    private String imageSrc;

    @Column(name = "quantity_state")
    @Enumerated(EnumType.STRING)
    private QuantityState quantityState;
    @Column(name = "product_state")
    @Enumerated(EnumType.STRING)
    private ProductState productState;
    @Column(name = "product_category")
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;
}
