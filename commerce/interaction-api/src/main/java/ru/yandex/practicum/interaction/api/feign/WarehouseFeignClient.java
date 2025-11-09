package ru.yandex.practicum.interaction.api.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.ReturnProductsRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

@FeignClient(name = "warehouse", fallback = WarehouseFeignFallback.class)
public interface WarehouseFeignClient {

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkProductsQuantity(@Valid @RequestBody ShoppingCartDto shoppingCartDto);

    @PostMapping("/api/v1/warehouse/assembly")
    BookedProductsDto assembly(@Valid @RequestBody AssemblyProductsForOrderRequest request);

    @PostMapping("/api/v1/warehouse/return")
    void returnProducts(@Valid @RequestBody ReturnProductsRequest request);
}
