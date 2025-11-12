package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.interaction.api.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.NewProductWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.OrderBookingAddDeliveryRequest;
import ru.yandex.practicum.interaction.api.dto.ReturnProductsRequest;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void addNewProduct(NewProductWarehouseRequest request);

    BookedProductsDto checkProductsQuantity(Map<UUID, Integer> products);

    void addSpecifiedProduct(AddProductToWarehouseRequest request);

    AddressDto getAddress();

    BookedProductsDto assembly(AssemblyProductsForOrderRequest request);

    void addDelivery(OrderBookingAddDeliveryRequest request);

    void returnProducts(ReturnProductsRequest request);
}
