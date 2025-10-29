package ru.yandex.practicum.shopping.cart.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "carts")
@Getter
@Setter
@Builder
public class Cart {
    // todo попоробовать везде перевести на UUID вместо String
    @Id
    @Column(name = "cart_id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String cartId;

    @Column(nullable = false, length = 100)
    private String username;

    @Column
    private boolean active;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartProduct> products;
}
