package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product implements ProductInfo {
    @Id
    @Column(name = "product_id", length = 36)
    private String productId;

    @Column
    private boolean fragile;

    @Column(precision = 15, scale = 2)
    @Min(1)
    private BigDecimal width;

    @Column(precision = 15, scale = 2)
    @Min(1)
    private BigDecimal height;

    @Column(precision = 15, scale = 2)
    @Min(1)
    private BigDecimal depth;

    @Column(precision = 15, scale = 2)
    @Min(1)
    private BigDecimal weight;

    @Column
    @PositiveOrZero
    private int quantity;
}
