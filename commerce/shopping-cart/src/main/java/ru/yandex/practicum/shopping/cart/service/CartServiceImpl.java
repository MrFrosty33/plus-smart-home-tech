package ru.yandex.practicum.shopping.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.logging.Loggable;
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

    private final String className = this.getClass().getSimpleName();

    @Override
    @Loggable
    @Cacheable(value = "carts", key = "#username")
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
    @CachePut(value = "carts", key = "#username")
    public ShoppingCartDto addProduct(String username, Map<String, Integer> products) {
        //todo проверка, есть ли товар на складе
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

        products.forEach((key, value) -> {
            CartProductEmbeddedId embeddedId = CartProductEmbeddedId.builder()
                    .productId(key)
                    .cartId(cart.getCartId())
                    .build();

            CartProduct cartProduct = CartProduct.builder()
                    .embeddedId(embeddedId)
                    .cart(cart)
                    .productId(key)
                    .quantity(value)
                    .build();

            cart.getProducts().add(cartProduct);
        });

        return cartMapper.toDto(cart);
    }

    @Override
    @Loggable
    @Transactional
    @CacheEvict(value = "carts", key = "#username")
    public void deactivateCart(String username) {

    }

    @Override
    @Loggable
    @Transactional
    @CachePut(value = "carts", key = "#username")
    public ShoppingCartDto removeProducts(String username, Set<String> productsId) {
        return null;
    }

    @Override
    @Loggable
    @Transactional
    @CachePut(value = "carts", key = "#username")
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        return null;
    }
}
