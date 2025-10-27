package ru.yandex.practicum.warehouse.model;

import java.math.BigDecimal;

public interface ProductInfo {
    String getProductId();

    boolean isFragile();

    BigDecimal getDepth();

    BigDecimal getWidth();

    BigDecimal getHeight();

    BigDecimal getWeight();

    int getQuantity();
}
