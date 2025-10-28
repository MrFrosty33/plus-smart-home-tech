package ru.yandex.practicum.shopping.cart.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "carts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Cart {
    @Id
    @Column(name = "cart_id", length = 36)
    //@GeneratedValue(strategy = GenerationType.UUID)
    private String cartId;

    @Column(nullable = false, length = 100)
    private String username;

    @Column
    private boolean active;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartProduct> products;

    public void addProduct(String productId, Integer quantity) {
        if (products == null) products = new HashSet<>();

        CartProduct existstingCartProduct = products.stream()
                .filter(p -> p.getEmbeddedId().getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        // если уже существует в корзине продукт, будет добавлено желаемое количество
        if (existstingCartProduct != null) {
            existstingCartProduct.setQuantity(existstingCartProduct.getQuantity() + quantity);
        } else {
            CartProductEmbeddedId embeddedId = CartProductEmbeddedId.builder()
                    .productId(productId)
                    .cartId(cartId)
                    .build();

            CartProduct cartProduct = CartProduct.builder()
                    .embeddedId(embeddedId)
                    .cart(this)
                    .quantity(quantity)
                    .build();

            products.add(cartProduct);
        }
    }
}
