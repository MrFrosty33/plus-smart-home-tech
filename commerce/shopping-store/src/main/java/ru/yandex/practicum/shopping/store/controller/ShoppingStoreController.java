package ru.yandex.practicum.shopping.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.dto.ProductCategory;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.api.feign.ShoppingStoreFeignClient;
import ru.yandex.practicum.shopping.store.service.StoreService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@Validated
public class ShoppingStoreController implements ShoppingStoreFeignClient {
    private final StoreService storeService;

    @GetMapping
    public Page<ProductDto> getWithPagination(@RequestParam ProductCategory category, Pageable pageable) {
        return storeService.getWithPagination(category, pageable);
    }

    @GetMapping("/{productId}")
    public ProductDto getById(@PathVariable UUID productId) {
        return storeService.getById(productId);
    }

    @PutMapping
    public ProductDto create(@Valid @RequestBody ProductDto productDto) {
        return storeService.create(productDto);
    }

    @PostMapping
    public ProductDto update(@Valid @RequestBody ProductDto productDto) {
        return storeService.update(productDto);
    }

    @PostMapping("/quantityState")
    public ProductDto updateQuantityState(SetProductQuantityStateRequest request) {
        return storeService.updateQuantityState(request);
    }

    @PostMapping("/removeProductFromStore")
    public ProductDto remove(@RequestBody UUID productId) {
        return storeService.remove(productId);
    }
}
