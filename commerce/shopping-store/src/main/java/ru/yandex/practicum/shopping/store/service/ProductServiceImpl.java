package ru.yandex.practicum.shopping.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.logging.Loggable;
import ru.yandex.practicum.shopping.store.model.Pageable;
import ru.yandex.practicum.shopping.store.model.SetProductQuantityStateRequest;
import ru.yandex.practicum.shopping.store.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    //todo подключить cache: @Cacheable (для добавления), @CachePut (для обновления), @CacheEvict (для удаления)
    // использовать можно Caffeine, настроить его и ограничить хранение в кэше по времени
    // @Loggable
    @Override
    @Loggable
    public List<ProductDto> getWithPagination(ProductCategory category, Pageable pageable) {
        return List.of();
    }

    @Override
    public ProductDto getById(String productId) {
        return null;
    }

    @Override
    public ProductDto create(ProductDto productDto) {
        return null;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        return null;
    }

    @Override
    public boolean updateQuantityState(SetProductQuantityStateRequest request) {
        return false;
    }

    @Override
    public boolean remove(String productId) {
        return false;
    }
}
