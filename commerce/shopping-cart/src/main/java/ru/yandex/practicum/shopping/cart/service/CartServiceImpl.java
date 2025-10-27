package ru.yandex.practicum.shopping.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.feign.WarehouseFeignClient;
import ru.yandex.practicum.interaction.api.logging.Loggable;
import ru.yandex.practicum.shopping.cart.exception.InternalServerException;
import ru.yandex.practicum.shopping.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.shopping.cart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.shopping.cart.mapper.CartMapper;
import ru.yandex.practicum.shopping.cart.model.Cart;
import ru.yandex.practicum.shopping.cart.model.CartProduct;
import ru.yandex.practicum.shopping.cart.model.CartProductEmbeddedId;
import ru.yandex.practicum.shopping.cart.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.shopping.cart.repository.CartProductRepository;
import ru.yandex.practicum.shopping.cart.repository.CartRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartProductRepository cartProductRepository;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final WarehouseFeignClient warehouseFeignClient;

    private final String className = this.getClass().getSimpleName();

    @Override
    @Loggable
    @Cacheable(value = "shopping-cart.carts", key = "#username")
    public ShoppingCartDto get(String username) {
        return cartMapper.toDto(cartRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("{}: no Cart found for username: {}", className, username);
            String message = "Cart for username: " + username + " cannot be found";
            String userMessage = "Cart not found";
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            return new NotAuthorizedUserException(message, userMessage, status);
        }));
    }

    @Override
    @Loggable
    @Transactional
    @CachePut(value = "shopping-cart.carts", key = "#username")
    public ShoppingCartDto addProduct(String username, Map<String, Integer> products) {
        Cart cart = cartRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("{}: no Cart found for username: {}", className, username);
            String message = "Cart for username: " + username + " cannot be found";
            String userMessage = "Cart not found";
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            return new NotAuthorizedUserException(message, userMessage, status);
        });

        if (cart.getProducts() == null) {
            cart.setProducts(new HashSet<>());
        }

        products.forEach(cart::addProduct);
        ShoppingCartDto cartDto = cartMapper.toDto(cart);

        BookedProductsDto bookedProductsDto = warehouseFeignClient.checkProductsQuantity(cartDto);
        // null присылает WarehouseFeignFallback
        if (bookedProductsDto == null) {
            log.warn("{}: warehouseFeignClient is unavailable — check products quantity request did not reach its destination.", className);
            String message = "warehouse feignClient not available";
            throw new InternalServerException(message);
        }
        //todo дальше уже наверно будет оформление заказа?
        return cartDto;
    }

    @Override
    @Loggable
    @Transactional
    @CacheEvict(value = "shopping-cart.carts", key = "#username")
    public void deactivateCart(String username) {
        // как это вообще использоваться будет?
        // нужно ли будет проверять, является ли корзина активна, прежде чем производить какие-либо действия с ней?
        Cart cart = cartRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("{}: no Cart found for username: {}", className, username);
            String message = "Cart for username: " + username + " cannot be found";
            String userMessage = "Cart not found";
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            return new NotAuthorizedUserException(message, userMessage, status);
        });

        cart.setActive(false);
    }

    @Override
    @Loggable
    @Transactional
    @CachePut(value = "shopping-cart.carts", key = "#username")
    public ShoppingCartDto removeProducts(String username, Set<String> productsId) {
        Cart cart = cartRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("{}: no Cart found for username: {}", className, username);
            String message = "Cart for username: " + username + " cannot be found";
            String userMessage = "Cart not found";
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            return new NotAuthorizedUserException(message, userMessage, status);
        });

        productsId.forEach((id) -> {
            CartProductEmbeddedId embeddedId = CartProductEmbeddedId.builder()
                    .cartId(cart.getCartId())
                    .productId(id)
                    .build();
            CartProduct cartProduct = cartProductRepository.findById(embeddedId).orElseThrow(() -> {
                log.warn("{}: no CartProduct found for embeddedId: {}", className, embeddedId);
                String message = "CartProduct for cartId: " + embeddedId.getCartId()
                        + " and productId:" + embeddedId.getProductId() + " cannot be found";
                String userMessage = "There is no Product in Cart";
                HttpStatus status = HttpStatus.UNAUTHORIZED;
                return new NoProductsInShoppingCartException(message, userMessage, status);
            });

            cart.getProducts().remove(cartProduct);
        });

        return cartMapper.toDto(cart);
    }

    @Override
    @Loggable
    @Transactional
    @CachePut(value = "shopping-cart.carts", key = "#username")
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        Cart cart = cartRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("{}: no Cart found for username: {}", className, username);
            String message = "Cart for username: " + username + " cannot be found";
            String userMessage = "Cart not found";
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            return new NotAuthorizedUserException(message, userMessage, status);
        });

        CartProductEmbeddedId embeddedId = CartProductEmbeddedId.builder()
                .cartId(cart.getCartId())
                .productId(request.getProductId())
                .build();

        CartProduct cartProduct = cartProductRepository.findById(embeddedId).orElseThrow(() -> {
            log.warn("{}: no CartProduct found for embeddedId: {}", className, embeddedId);
            String message = "CartProduct for cartId: " + embeddedId.getCartId()
                    + " and productId:" + embeddedId.getProductId() + " cannot be found";
            String userMessage = "There is no Product in Cart";
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            return new NoProductsInShoppingCartException(message, userMessage, status);
        });

        cartProduct.setQuantity(request.getNewQuantity());

        return cartMapper.toDto(cart);
    }
}
