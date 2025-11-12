package ru.yandex.practicum.interaction.api.feign;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.OrderBookingAddDeliveryRequest;
import ru.yandex.practicum.interaction.api.dto.ReturnProductsRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

@Component
public class WarehouseFeignFallback implements WarehouseFeignClient {
    @Override
    public BookedProductsDto checkProductsQuantity(ShoppingCartDto shoppingCartDto) {
        return null;
    }

    @Override
    public BookedProductsDto assemblyOrder(AssemblyProductsForOrderRequest request) {
        return null;
    }

    @Override
    public void returnProducts(ReturnProductsRequest request) {

    }

    @Override
    public void addDelivery(OrderBookingAddDeliveryRequest request) {

    }

    @Override
    public AddressDto getAddress() {
        // пускай будет тут так, чтобы не усложнять лишней проверкой на null код orderServiceImpl.create()
        String CURRENT_ADDRESS = "ADDRESS_1";

        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }
}
