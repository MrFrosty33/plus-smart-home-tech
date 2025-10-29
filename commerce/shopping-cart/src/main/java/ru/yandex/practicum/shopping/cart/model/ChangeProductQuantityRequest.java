package ru.yandex.practicum.shopping.cart.model;

import lombok.Data;

import java.util.UUID;

@Data
public class ChangeProductQuantityRequest {
    private final UUID productId;
    private final int newQuantity;
}
