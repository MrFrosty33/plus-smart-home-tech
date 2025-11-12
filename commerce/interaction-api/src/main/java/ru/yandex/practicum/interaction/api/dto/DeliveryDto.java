package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class DeliveryDto {
    private final UUID deliveryId;

    @Valid
    private final AddressDto fromAddress;
    @Valid
    private final AddressDto toAddress;

    private final UUID orderId;

    private final DeliveryState deliveryState;
}
