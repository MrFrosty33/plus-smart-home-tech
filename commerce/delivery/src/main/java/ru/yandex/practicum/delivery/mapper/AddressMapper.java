package ru.yandex.practicum.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.delivery.model.Address;
import ru.yandex.practicum.interaction.api.dto.AddressDto;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    @Mapping(target = "addressId", ignore = true)
    Address toEntity(AddressDto dto);

    AddressDto toDto(Address entity);
}
