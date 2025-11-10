package ru.yandex.practicum.interaction.api.feign;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.api.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.ReturnProductsRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

@Component
public class WarehouseFeignFallback implements WarehouseFeignClient {
    @Override
    public BookedProductsDto checkProductsQuantity(ShoppingCartDto shoppingCartDto) {
        return null;
    }

    @Override
    public BookedProductsDto assemblyOrder(AssemblyProductsForOrderRequest request) {
        return null;
    }

    @Override
    public void returnProducts(ReturnProductsRequest request) {

    }
}
