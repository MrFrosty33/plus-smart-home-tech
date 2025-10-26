package ru.yandex.practicum.warehouse.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CachedProduct {
    private String productId;
    private boolean fragile;
    private double width;
    private double height;
    private double depth;
    private double weight;
    private int quantity;
}
