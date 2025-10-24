package ru.yandex.practicum.shopping.store.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.shopping.store.model.SetProductQuantityStateRequest;

import java.util.List;

public interface ProductService {
    List<ProductDto> getWithPagination(ProductCategory category, Pageable pageable);

    ProductDto getById(String productId);

    ProductDto create(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    boolean updateQuantityState(SetProductQuantityStateRequest request);

    boolean remove(String productId);
}
