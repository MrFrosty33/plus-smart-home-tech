package ru.yandex.practicum.shopping.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.interaction.api.dto.ProductCategory;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.SetProductQuantityStateRequest;

public interface ProductService {
    Page<ProductDto> getWithPagination(ProductCategory category, Pageable pageable);

    ProductDto getById(String productId);

    ProductDto create(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    ProductDto updateQuantityState(SetProductQuantityStateRequest request);

    ProductDto remove(String productId);
}
