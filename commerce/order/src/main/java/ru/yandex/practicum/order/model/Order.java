package ru.yandex.practicum.order.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @Column(name = "order_id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;
    @Column(name = "shopping_cart_id", updatable = false, nullable = false)
    private UUID shoppingCartId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "orders_products",
            joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Integer> products = new HashMap<>();

    @Column(name = "payment_id", updatable = false, nullable = false)
    private UUID paymentId;
    @Column(name = "delivery_id", updatable = false, nullable = false)
    private UUID deliveryId;

    @Positive
    @Column(name = "delivery_weight")
    private BigDecimal deliveryWeight;
    @Positive
    @Column(name = "delivery_volume")
    private BigDecimal deliveryVolume;

    @Column
    private boolean fragile;

    @Positive
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @Positive
    @Column(name = "delivery_price")
    private BigDecimal deliveryPrice;
    @Positive
    @Column(name = "products_price")
    private BigDecimal productsPrice;
}
