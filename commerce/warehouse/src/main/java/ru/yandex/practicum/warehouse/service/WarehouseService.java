package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.interaction.api.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.NewProductWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

public interface WarehouseService {
    void addNewProduct(NewProductWarehouseRequest request);

    BookedProductsDto checkProductsQuantity(ShoppingCartDto shoppingCartDto);

    void addSpecifiedProduct(AddProductToWarehouseRequest request);

    AddressDto getAddress();
}
