package ru.yandex.practicum.shopping.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
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
import ru.yandex.practicum.interaction.api.dto.ProductState;
import ru.yandex.practicum.interaction.api.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.api.logging.Loggable;
import ru.yandex.practicum.shopping.store.exception.ProductNotFoundException;
import ru.yandex.practicum.shopping.store.mapper.ProductMapper;
import ru.yandex.practicum.shopping.store.model.Product;
import ru.yandex.practicum.shopping.store.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final String className = this.getClass().getSimpleName();
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final CacheManager cacheManager;

    @Override
    @Loggable
    public Page<ProductDto> getWithPagination(ProductCategory category, Pageable pageable) {
        // лучше бы накинуть на главный класс , но тогда тест отваливается
        //@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
        Page<Product> page = productRepository.findAllByProductCategory(category, pageable);
        return page.map(productMapper::toDto);
    }

    @Override
    @Loggable
    @Cacheable(value = "shopping-store.products", key = "#productId")
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
    @CachePut(value = "shopping-store.products", key = "#result.productId")
    @Transactional
    public ProductDto create(ProductDto productDto) {
        //todo а что, если уже существует продукт? Такого быть не может?
        Product entity = productMapper.toEntity(productDto);
        entity = productRepository.save(entity);
        return productMapper.toDto(entity);
    }

    @Override
    @Loggable
    @CachePut(value = "shopping-store.products", key = "#result.productId")
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
    @Transactional
    public ProductDto updateQuantityState(SetProductQuantityStateRequest request) {
        //todo тут похоже корявый openApi/ тест, одно из двух)))
        // ибо он требует openApi требует
        /*
        "requestBody": {
          "description": "Запрос на изменение статуса товара в магазине, например: \"Закончился\", \"Мало\" и т.д.",
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/SetProductQuantityStateRequest"
              }
            }
          }
        }
         */
        //todo а в тесте
        // {{baseUrl}}{{shopping-store-port}}/api/v1/shopping-store/quantityState?productId={{product_id}}&quantityState={{quantity_state}}

        // короче всё ещё проблема с возвращаемым объектом.
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> {
            log.warn("{}: quantity state update failure - cannot find Product with id: {}", className, request.getProductId());
            String message = "Product with id: " + request.getProductId() + " cannot be found";
            String userMessage = "Product not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new ProductNotFoundException(message, userMessage, status);
        });

        product.setQuantityState(request.getQuantityState());

        // метод не возвращает дто, не забываем обновить данные в кэше
        cacheManager.getCache("shopping-store.products").put(product.getProductId(), product);
        return productMapper.toDto(product);
    }

    @Override
    @Loggable
    @Transactional
    @CacheEvict(value = "shopping-store.products", key = "#productId")
    public boolean remove(String productId) {
        //todo почему-то не находит он запись в БД по тому id, что приходит
        Product product = productRepository.findById(productId).orElseThrow(() -> {
            log.warn("{}: remove product failure - cannot find Product with id: {}", className, productId);
            String message = "Product with id: " + productId + " cannot be found";
            String userMessage = "Product not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new ProductNotFoundException(message, userMessage, status);
        });

        product.setProductState(ProductState.DEACTIVATE);
        return true;
    }

}
