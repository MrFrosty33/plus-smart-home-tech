package ru.yandex.practicum.shopping.store.controller;

import lombok.RequiredArgsConstructor;
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
import ru.yandex.practicum.shopping.store.model.SetProductQuantityStateRequest;
import ru.yandex.practicum.shopping.store.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@Validated
public class ShoppingStoreController {
    private final ProductService productService;

    @GetMapping
    public List<ProductDto> getWithPagination(@RequestParam ProductCategory category, Pageable pageable) {
        return productService.getWithPagination(category, pageable);
    }

    @GetMapping("/{productId}")
    public ProductDto getById(@PathVariable String productId) {
        return productService.getById(productId);
    }

    @PutMapping
    public ProductDto create(@RequestBody ProductDto productDto) {
        return productService.create(productDto);
    }

    @PostMapping
    public ProductDto update(@RequestBody ProductDto productDto) {
        return productService.update(productDto);
    }

    @PostMapping("/quantityState")
    public boolean updateQuantityState(@RequestBody SetProductQuantityStateRequest request) {
        return productService.updateQuantityState(request);
    }

    @PostMapping("/removeProductFromStore")
    public boolean remove(@RequestBody String productId) {
        return productService.remove(productId);
    }
}
