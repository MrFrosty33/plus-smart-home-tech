package ru.yandex.practicum.shopping.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.shopping.cart.model.Cart;
import ru.yandex.practicum.shopping.cart.model.CartProduct;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(source = "cartId", target = "shoppingCartId")
    public ShoppingCartDto toDto(Cart cart);

    default Map<String, Integer> mapProducts(Set<CartProduct> products) {
        if (products == null) return null;
        return products.stream()
                .collect(Collectors.toMap(
                        cartProduct -> cartProduct.getEmbeddedId().getProductId(),
                        cartProduct -> cartProduct.getQuantity()
                        )
                );
    }
}
