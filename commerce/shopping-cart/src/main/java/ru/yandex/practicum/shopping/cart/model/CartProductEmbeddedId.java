package ru.yandex.practicum.shopping.cart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class CartProductEmbeddedId implements Serializable {

    @Column(name = "cart_id", nullable = false, length = 36)
    private String cartId;

    @Column(name = "product_id", nullable = false, length = 36)
    private String productId;
}