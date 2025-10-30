package ru.yandex.practicum.shopping.cart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartProductEmbeddedId implements Serializable {
    @Column(name = "cart_id", nullable = false, updatable = false)
    private UUID cartId;

    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;
}