package ru.yandex.practicum.shopping.cart.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.exception.NotAuthorizedUserException;
import ru.yandex.practicum.interaction.api.feign.ShoppingCartFeignClient;
import ru.yandex.practicum.shopping.cart.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.shopping.cart.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
@Validated
public class ShoppingCartController implements ShoppingCartFeignClient {
    private final CartService cartService;

    @GetMapping
    public ShoppingCartDto getByUsername(@RequestParam String username) {
        checkUsername(username);
        return cartService.getByUsername(username);
    }

    @GetMapping("/allPast")
    public List<ShoppingCartDto> getAllPastByUsername(@RequestParam String username) {
        checkUsername(username);
        return cartService.getAllPastByUsername(username);
    }

    @PutMapping
    public ShoppingCartDto addProduct(@RequestParam
                                      String username,
                                      @RequestBody
                                      @NotEmpty
                                      Map<UUID, Integer> products) {
        checkUsername(username);
        return cartService.addProduct(username, products);
    }

    @DeleteMapping
    public void deactivateCart(@RequestParam String username) {
        checkUsername(username);
        cartService.deactivateCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProducts(@RequestParam String username,
                                          @RequestBody Set<UUID> productsId) {
        checkUsername(username);
        return cartService.removeProducts(username, productsId);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantity(@RequestParam String username,
                                          @RequestBody ChangeProductQuantityRequest request) {
        checkUsername(username);
        return cartService.changeQuantity(username, request);
    }

    //todo пока имя пользователя всегда валидно, только если не пусто, потом будет авторизация
    private void checkUsername(String username) {
        if (username == null || username.isBlank()) {
            String message = "Received null / blank username";
            String userMessage = "Username must be provided and cannot be blank";
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            throw new NotAuthorizedUserException(message, userMessage, status);
        }
    }
}
