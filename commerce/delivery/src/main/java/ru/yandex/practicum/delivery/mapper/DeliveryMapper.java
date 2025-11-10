package ru.yandex.practicum.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.interaction.api.dto.DeliveryDto;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "fromAddress", ignore = true)
    @Mapping(target = "toAddress", ignore = true)
    Delivery toEntity(DeliveryDto dto);

    @Mapping(target = "fromAddress", ignore = true)
    @Mapping(target = "toAddress", ignore = true)
    DeliveryDto toDto(Delivery entity);
}
