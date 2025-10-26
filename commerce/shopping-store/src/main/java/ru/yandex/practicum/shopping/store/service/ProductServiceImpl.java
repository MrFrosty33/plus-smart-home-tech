package ru.yandex.practicum.shopping.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.ProductCategory;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.api.logging.Loggable;
import ru.yandex.practicum.shopping.store.exception.ProductNotFoundException;
import ru.yandex.practicum.shopping.store.mapper.ProductMapper;
import ru.yandex.practicum.shopping.store.model.Product;
import ru.yandex.practicum.shopping.store.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final String className = this.getClass().getSimpleName();
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Loggable
    public List<ProductDto> getWithPagination(ProductCategory category, Pageable pageable) {
        Page<Product> page = productRepository.findAllByProductCategory(category, pageable);
        return page.get().map(productMapper::toDto).toList();
    }

    @Override
    @Loggable
    @Cacheable(value = "products", key = "#productId")
    public ProductDto getById(String productId) {
        return productMapper.toDto(productRepository.findById(productId).orElseThrow(() -> {
            log.warn("{}: cannot find Product with id: {}", className, productId);
            String message = "Product with id: " + productId + " cannot be found";
            String userMessage = "Product not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new ProductNotFoundException(message, userMessage, status);
        }));
    }

    @Override
    @Loggable
    @CachePut(value = "products", key = "#productDto.productId")
    @Transactional
    public ProductDto create(ProductDto productDto) {
        //todo а что, если уже существует продукт? Такого быть не может
        Product entity = productMapper.toEntity(productDto);
        entity = productRepository.save(entity);
        return productMapper.toDto(entity);
    }

    @Override
    @Loggable
    @CachePut(value = "products", key = "#productDto.productId")
    @Transactional
    public ProductDto update(ProductDto productDto) {
        Product old = productRepository.findById(productDto.getProductId()).orElseThrow(() -> {
            log.warn("{}: update failure - cannot find Product with id: {}", className, productDto.getProductId());
            String message = "Product with id: " + productDto.getProductId() + " cannot be found";
            String userMessage = "Product not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new ProductNotFoundException(message, userMessage, status);
        });

        Product fresh = productMapper.toEntity(productDto);
        if (old.equals(fresh)) {
            return productDto;
        }

        productRepository.save(fresh);
        return productMapper.toDto(fresh);
    }

    @Override
    @Loggable
    @CachePut(value = "products", key = "#request.productId")
    @Transactional
    public boolean updateQuantityState(SetProductQuantityStateRequest request) {
        Product old = productRepository.findById(request.getProductId()).orElseThrow(() -> {
            log.warn("{}: quantity state update failure - cannot find Product with id: {}", className, request.getProductId());
            String message = "Product with id: " + request.getProductId() + " cannot be found";
            String userMessage = "Product not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new ProductNotFoundException(message, userMessage, status);
        });

        old.setQuantityState(request.getQuantityState());
        return true;
    }

    @Override
    @Loggable
    @CacheEvict(value = "products", key = "#productId")
    @Transactional
    public boolean remove(String productId) {
        return productRepository.deleteByProductId(productId) > 0;
    }
}
