package ru.yandex.practicum.warehouse.model;

public interface ProductInfo {
    String getProductId();

    boolean isFragile();

    double getDepth();

    double getWidth();

    double getHeight();

    double getWeight();

    int getQuantity();
}
