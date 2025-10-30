package ru.yandex.practicum.warehouse.model;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductInfo {
    UUID getProductId();

    boolean isFragile();

    BigDecimal getDepth();

    BigDecimal getWidth();

    BigDecimal getHeight();

    BigDecimal getWeight();

    int getQuantity();
}
