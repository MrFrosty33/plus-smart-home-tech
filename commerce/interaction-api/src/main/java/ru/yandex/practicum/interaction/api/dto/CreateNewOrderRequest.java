package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateNewOrderRequest {
    private final ShoppingCartDto shoppingCart;
    @Valid
    private final AddressDto deliveryAddress;
}
