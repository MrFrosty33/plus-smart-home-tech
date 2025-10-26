package ru.yandex.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.NewProductWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.logging.Loggable;
import ru.yandex.practicum.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.mapper.ProductMapper;
import ru.yandex.practicum.warehouse.model.CachedProduct;
import ru.yandex.practicum.warehouse.model.Product;
import ru.yandex.practicum.warehouse.model.ProductInfo;
import ru.yandex.practicum.warehouse.repository.ProductRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.DoubleAdder;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final String className = this.getClass().getSimpleName();

    private CacheManager cacheManager;

    @Override
    @Loggable
    @Transactional
    public void addNewProduct(NewProductWarehouseRequest request) {
        productRepository.findById(request.getProductId())
                .ifPresent(product -> {
                    log.warn("{}: product with id: {} already exists in warehouse", className, request.getProductId());
                    String message = "Product with id: " + request.getProductId() + " already exists in warehouse";
                    String userMessage = "Product already exists";
                    HttpStatus status = HttpStatus.BAD_REQUEST;
                    throw new SpecifiedProductAlreadyInWarehouseException(message, userMessage, status);
                });

        Product product = productMapper.toEntity(request);
        productRepository.save(product);

        CachedProduct cachedProduct = productMapper.toCachedProduct(product);
        cacheManager.getCache("products").put(cachedProduct.getProductId(), cachedProduct);
    }

    @Override
    @Loggable
    public BookedProductsDto checkProductsQuantity(ShoppingCartDto shoppingCartDto) {
        AtomicBoolean notEnoughFlag = new AtomicBoolean(false);
        Set<String> notEnoughProducts = new HashSet<>();

        DoubleAdder deliveryVolume = new DoubleAdder();
        DoubleAdder deliveryWeight = new DoubleAdder();
        AtomicBoolean fragile = new AtomicBoolean(false);

        shoppingCartDto.getProducts().entrySet()
                .forEach((entry) -> {
                    Cache.ValueWrapper valueWrapper = cacheManager.getCache("products").get(entry.getKey());

                    // проверка, хранится ли в кэше
                    if (valueWrapper != null) {
                        CachedProduct product = ((CachedProduct) valueWrapper.get());
                        checkQuantityAndCalculateDeliveryParams(product, entry.getValue(),
                                notEnoughFlag, notEnoughProducts, deliveryVolume, deliveryWeight, fragile);
                    } else {
                        //todo что если товар не существует?
                        // пока сделаю проброс исключения, но может и не требуется
                        Product product = productRepository.findById(entry.getKey()).orElseThrow(() -> {
                            log.warn("{}: quantity of Product with id: {} is less, than required", className, entry.getKey());
                            String message = "quantity of Product with id: " + entry.getKey() + " is less, than required";
                            String userMessage = "Not enough products on warehouse";
                            HttpStatus status = HttpStatus.NOT_FOUND;
                            throw new ProductInShoppingCartLowQuantityInWarehouseException(message, userMessage, status);
                        });

                        checkQuantityAndCalculateDeliveryParams(product, entry.getValue(),
                                notEnoughFlag, notEnoughProducts, deliveryVolume, deliveryWeight, fragile);
                    }
                });

        if (notEnoughFlag.get()) {
            log.warn("{}: quantity of Products with ids: {} is less, than required", className, notEnoughProducts);
            String message = "quantity of Products with ids: " + notEnoughProducts + " is less, than required";
            String userMessage = "Not enough products on warehouse";
            HttpStatus status = HttpStatus.NOT_FOUND;
            throw new ProductInShoppingCartLowQuantityInWarehouseException(message, userMessage, status);
        }

        return BookedProductsDto.builder()
                .deliveryVolume(deliveryVolume.doubleValue())
                .deliveryWeight(deliveryWeight.doubleValue())
                .fragile(fragile.get())
                .build();
    }

    @Override
    public void addSpecifiedProduct(AddProductToWarehouseRequest request) {

    }

    @Override
    public AddressDto getAddress() {
        return null;
    }

    private void checkQuantityAndCalculateDeliveryParams(ProductInfo product, Integer requiredQuantity,
                                                         AtomicBoolean notEnoughFlag, Set<String> notEnoughProducts,
                                                         DoubleAdder deliveryVolume, DoubleAdder deliveryWeight,
                                                         AtomicBoolean fragile) {
        if (product.getQuantity() < requiredQuantity) {
            notEnoughFlag.set(true);
            notEnoughProducts.add(product.getProductId());
        }

        deliveryVolume.add(product.getDepth() * product.getWidth() * product.getHeight() * requiredQuantity);
        deliveryWeight.add(product.getWeight() * requiredQuantity);
        if (product.isFragile()) fragile.set(true);
    }
}
