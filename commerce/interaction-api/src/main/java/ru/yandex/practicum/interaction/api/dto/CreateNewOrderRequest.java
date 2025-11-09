package ru.yandex.practicum.interaction.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateNewOrderRequest {
    private final ShoppingCartDto shoppingCart;
    private final AddressDto deliveryAddress;
}
