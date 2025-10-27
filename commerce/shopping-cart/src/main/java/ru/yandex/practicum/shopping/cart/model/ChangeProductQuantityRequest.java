package ru.yandex.practicum.shopping.cart.model;

import lombok.Data;

@Data
public class ChangeProductQuantityRequest {
    private final String productId;
    private final int newQuantity;
}
