package ru.yandex.practicum.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.interaction.api.dto.PaymentDto;
import ru.yandex.practicum.payment.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "paymentId", ignore = true)
    Payment toEntity(PaymentDto dto);

    PaymentDto toDto(Payment entity);
}
