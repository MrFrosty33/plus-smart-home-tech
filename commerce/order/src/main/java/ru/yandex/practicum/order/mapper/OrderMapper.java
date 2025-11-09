package ru.yandex.practicum.order.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.order.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toEntity(OrderDto dto);

    OrderDto toDto(Order entity);
}
