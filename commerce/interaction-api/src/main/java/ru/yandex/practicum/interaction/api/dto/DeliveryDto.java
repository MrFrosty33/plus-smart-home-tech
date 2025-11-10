package ru.yandex.practicum.interaction.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class DeliveryDto {
    private final UUID deliveryId;

    private final AddressDto fromAddress;
    private final AddressDto toAddress;

    private final UUID orderId;

    private final DeliveryState deliveryState;
}
