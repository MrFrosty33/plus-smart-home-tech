package ru.yandex.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.yandex.practicum.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.mapper.ProductMapper;
import ru.yandex.practicum.warehouse.model.Product;
import ru.yandex.practicum.warehouse.repository.ProductRepository;

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
        //todo разобраться с кэшем тут и в других сервисах глянуть
        // если не возвращает ничего - то и не помещает в кэш
        // помещать вручную?
        productRepository.findById(request.getProductId())
                .ifPresent(product -> {
                    String message = "Product with id: " + request.getProductId() + " already exists in warehouse";
                    String userMessage = "Product already exists";
                    HttpStatus status = HttpStatus.BAD_REQUEST;
                    throw new SpecifiedProductAlreadyInWarehouseException(message, userMessage, status);
                });

        Product product = productMapper.toEntity(request);
        productRepository.save(product);

        // лучше хранить дто
        cacheManager.getCache("products").put(product.getProductId(), product);
    }

    @Override
    @Loggable
    public BookedProductsDto checkProductsQuantity(ShoppingCartDto shoppingCartDto) {
        shoppingCartDto.getProducts().entrySet().stream()
                .forEach((entry) -> {
                    productRepository
                });
        return null;
    }

    @Override
    public void addSpecifiedProduct(AddProductToWarehouseRequest request) {

    }

    @Override
    public AddressDto getAddress() {
        return null;
    }
}
