package ru.yandex.practicum.shopping.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {
    private final String className = this.getClass().getSimpleName();
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    //private final CacheManager cacheManager;

    @Override
    @Loggable
    public Page<ProductDto> getWithPagination(ProductCategory category, Pageable pageable) {
        Page<Product> page = productRepository.findAllByProductCategory(category, pageable);
        return page.map(productMapper::toDto);
    }

    @Override
    @Loggable
    //Cacheable(value = "shopping-store.products", key = "#productId")
    public ProductDto getById(UUID productId) {
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
    //@CachePut(value = "shopping-store.products", key = "#result.productId")
    @Transactional
    public ProductDto create(ProductDto productDto) {
        Product entity = productMapper.toEntity(productDto);
        entity = productRepository.saveAndFlush(entity);
        return productMapper.toDto(entity);
    }

    @Override
    @Loggable
    //@CachePut(value = "shopping-store.products", key = "#result.productId")
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
    //@CachePut(value = "shopping-store.products", key = "#request.productId")
    public ProductDto updateQuantityState(SetProductQuantityStateRequest request) {

        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> {
            log.warn("{}: quantity state update failure - cannot find Product with id: {}", className, request.getProductId());
            String message = "Product with id: " + request.getProductId() + " cannot be found";
            String userMessage = "Product not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new ProductNotFoundException(message, userMessage, status);
        });

        product.setQuantityState(request.getQuantityState());

        // метод не возвращает дто, не забываем обновить данные в кэше
        //cacheManager.getCache("shopping-store.products").put(product.getProductId(), product);
        return productMapper.toDto(product);
    }

    @Override
    @Loggable
    @Transactional
    //@CacheEvict(value = "shopping-store.products", key = "#result.productId")
    public ProductDto remove(UUID productId) {
        // todo при работе с кэшем была проблема с кавычками в id. обратить внимание при возвращении кэша

        //Cache.ValueWrapper valueWrapper = cacheManager.getCache("shopping-store.products").get(correctProductId);
        Product product;

//        boolean cachedProduct = false;
//        if (valueWrapper != null) {
//            ProductDto productDto = ((ProductDto) valueWrapper.get());
//            product = productMapper.toEntity(productDto);
//            cachedProduct = true;
//        } else {
//            // верни меня сюда
//        }

        // ^^^^^^^^^^
        product = productRepository.findById(productId).orElseThrow(() -> {
            log.warn("{}: remove product failure - cannot find Product with id: {}", className, productId);
            String message = "Product with id: " + productId + " cannot be found";
            String userMessage = "Product not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new ProductNotFoundException(message, userMessage, status);
        });


        product.setProductState(ProductState.DEACTIVATE);

        //if (cachedProduct) productRepository.save(product);

        return productMapper.toDto(product);
    }
}
