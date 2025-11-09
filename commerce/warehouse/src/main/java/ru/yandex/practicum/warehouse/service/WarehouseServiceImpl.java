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
import ru.yandex.practicum.interaction.api.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.NewProductWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.QuantityState;
import ru.yandex.practicum.interaction.api.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interaction.api.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.interaction.api.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.interaction.api.feign.ShoppingStoreFeignClient;
import ru.yandex.practicum.interaction.api.logging.Loggable;
import ru.yandex.practicum.warehouse.mapper.ProductMapper;
import ru.yandex.practicum.warehouse.model.CachedProduct;
import ru.yandex.practicum.warehouse.model.OrderBooking;
import ru.yandex.practicum.warehouse.model.Product;
import ru.yandex.practicum.warehouse.model.ProductInfo;
import ru.yandex.practicum.warehouse.repository.OrderBookingRepository;
import ru.yandex.practicum.warehouse.repository.ProductRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {
    private final ProductRepository productRepository;
    private final OrderBookingRepository orderBookingRepository;
    private final ProductMapper productMapper;
    private final ShoppingStoreFeignClient shoppingStoreFeignClient;
    private final String className = this.getClass().getSimpleName();

    // тут с кэшем везде вручную, ибо кэш вставляется из возвращаемого значения
    // а возвращаемых значения у методов либо отсутствуют, либо разные
    private final CacheManager cacheManager;

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
        cacheManager.getCache("warehouse.products").put(cachedProduct.getProductId(), cachedProduct);
    }

    @Override
    @Loggable
    public BookedProductsDto checkProductsQuantity(Map<UUID, Integer> products) {
        // переделал параметры метода, было ShoppingCartDto, стал чисто список продуктов
        // во избежания дублирования кода, ибо в assembly используется тот же алгоритм проверки
        AtomicBoolean notEnoughFlag = new AtomicBoolean(false);
        Set<UUID> notEnoughProducts = new HashSet<>();

        DoubleAdder deliveryVolume = new DoubleAdder();
        DoubleAdder deliveryWeight = new DoubleAdder();
        AtomicBoolean fragile = new AtomicBoolean(false);

        products.entrySet()
                .forEach((entry) -> {
                    Cache.ValueWrapper valueWrapper = cacheManager.getCache("warehouse.products").get(entry.getKey());

                    // проверка, хранится ли в кэше
                    if (valueWrapper != null) {
                        CachedProduct product = ((CachedProduct) valueWrapper.get());
                        checkQuantityAndCalculateDeliveryParams(product, entry.getValue(),
                                notEnoughFlag, notEnoughProducts, deliveryVolume, deliveryWeight, fragile);
                    } else {
                        Product product = productRepository.findById(entry.getKey()).orElseThrow(() -> {
                            log.warn("{}: cannot find Product with id: {}", className, entry.getKey());
                            String message = "Product with id: " + entry.getValue() + " cannot be found";
                            String userMessage = "Product not found";
                            HttpStatus status = HttpStatus.NOT_FOUND;
                            return new NoSpecifiedProductInWarehouseException(message, userMessage, status);
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

        BigDecimal resultVolume = BigDecimal.valueOf(deliveryVolume.doubleValue()).setScale(2, RoundingMode.UP);
        BigDecimal resultWeight = BigDecimal.valueOf(deliveryWeight.doubleValue()).setScale(2, RoundingMode.UP);

        return BookedProductsDto.builder()
                .deliveryVolume(resultVolume)
                .deliveryWeight(resultWeight)
                .fragile(fragile.get())
                .build();
    }

    @Override
    @Loggable
    @Transactional
    public void addSpecifiedProduct(AddProductToWarehouseRequest request) {
        Cache.ValueWrapper valueWrapper = cacheManager.getCache("warehouse.products").get(request.getProductId());
        Product product;

        boolean cachedProductFlag = false;
        if (valueWrapper != null) {
            CachedProduct cachedProduct = ((CachedProduct) valueWrapper.get());
            product = productMapper.toEntity(cachedProduct);
            cachedProductFlag = true;

            cachedProduct.setQuantity(product.getQuantity() + request.getQuantity());
            cacheManager.getCache("warehouse.products").put(request.getProductId(), cachedProduct);
        } else {
            product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> {
                        log.warn("{}: cannot find Product with id: {}", className, request.getProductId());
                        String message = "Product with id: " + request.getProductId() + " cannot be found";
                        String userMessage = "No product information found";
                        HttpStatus status = HttpStatus.BAD_REQUEST;
                        return new NoSpecifiedProductInWarehouseException(message, userMessage, status);
                    });
        }

        product.setQuantity(product.getQuantity() + request.getQuantity());
        if (cachedProductFlag) productRepository.save(product);

        //todo я был уверен, что нужно обновлять количество параллельно ещё и в магазине
        // оказывается, этого делать не нужно. На этом этапе или в принципе?
        // хотя, будто бы напрашивается по-логике при добавлении на склад обновить информацию и в магазине.
        // ну ладно. Убрал обновление - тесты проходят))))
//        ProductDto feignUpdateRequestResult = sendUpdateQuantityRequestToShoppingStore(product.getProductId(), product.getQuantity());
//
//        // null присылает ShoppingStoreFeignFallback
//        if (feignUpdateRequestResult == null) {
//            log.warn("{}: shoppingStoreFeignClient is unavailable — update quantity request did not reach its destination.", className);
//            String message = "Shopping-store feignClient not available";
//            throw new InternalServerException(message);
//        }
    }

    @Override
    @Loggable
    @Transactional
    public BookedProductsDto assembly(AssemblyProductsForOrderRequest request) {
        // этот метод делает всё то же, что и checkProductsQuantity, но ещё и уменьшает количество товаров
        // так что сперва проверяем количество
        BookedProductsDto result = checkProductsQuantity(request.getProducts());

        List<Product> productsToSave = new ArrayList<>();
        request.getProducts().entrySet().forEach((entry) -> {
            Cache.ValueWrapper valueWrapper = cacheManager.getCache("warehouse.products").get(entry.getKey());
            Product product;

            // проверка, хранится ли в кэше
            if (valueWrapper != null) {
                CachedProduct cachedProduct = ((CachedProduct) valueWrapper.get());
                product = productMapper.toEntity(cachedProduct);
            } else {
                product = productRepository.findById(entry.getKey()).orElseThrow(() -> {
                    log.warn("{}: cannot find Product with id: {}", className, entry.getKey());
                    String message = "Product with id: " + entry.getValue() + " cannot be found";
                    String userMessage = "Product not found";
                    HttpStatus status = HttpStatus.NOT_FOUND;
                    return new NoSpecifiedProductInWarehouseException(message, userMessage, status);
                });
            }

            // потом отнимаем
            product.setQuantity(product.getQuantity() - entry.getValue());
            productsToSave.add(product);
        });

        productRepository.saveAll(productsToSave);

        OrderBooking orderBooking = OrderBooking.builder()
                .orderBookingId(request.getOrderId())
                .products(productsToSave.stream()
                        .collect(Collectors.toMap(Product::getProductId, Product::getQuantity)))
                .build();

        // и в конце бронируем
        orderBookingRepository.save(orderBooking);
        return result;
    }

    @Override
    @Loggable
    public AddressDto getAddress() {
        //todo в дальнейшем будет реализован
        // на данный момент базовый алгоритм выглядит так
        String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
        String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    @Loggable
    private void checkQuantityAndCalculateDeliveryParams(ProductInfo product, Integer requiredQuantity,
                                                         AtomicBoolean notEnoughFlag, Set<UUID> notEnoughProducts,
                                                         DoubleAdder deliveryVolume, DoubleAdder deliveryWeight,
                                                         AtomicBoolean fragile) {
        if (product.getQuantity() < requiredQuantity) {
            notEnoughFlag.set(true);
            notEnoughProducts.add(product.getProductId());
        }

        deliveryVolume.add(product.getDepth().doubleValue() * product.getWidth().doubleValue()
                * product.getHeight().doubleValue() * requiredQuantity);
        deliveryWeight.add(product.getWeight().doubleValue() * requiredQuantity);
        if (product.isFragile()) fragile.set(true);
    }

    @Loggable
    private ProductDto sendUpdateQuantityRequestToShoppingStore(UUID productId, int quantity) {
        if (quantity == 0) {
            return shoppingStoreFeignClient
                    .updateQuantityState(productId, QuantityState.ENDED);
        }
        if (quantity > 0 && quantity < 10) {
            return shoppingStoreFeignClient
                    .updateQuantityState(productId, QuantityState.FEW);
        }
        if (quantity > 10 && quantity < 100) {
            return shoppingStoreFeignClient
                    .updateQuantityState(productId, QuantityState.ENOUGH);
        }
        if (quantity > 100) {
            return shoppingStoreFeignClient
                    .updateQuantityState(productId, QuantityState.MANY);
        }

        log.warn("{}: in sendUpdateQuantityRequestToShoppingStore() " +
                "quantity is not covered by if-else cases, quantity: {} ", className, quantity);
        return null;
    }
}
