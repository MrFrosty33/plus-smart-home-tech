package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AddressDto {
    @Max(100)
    private final String country;

    @Max(100)
    private final String city;

    @Max(1000)
    private final String street;

    @Max(100)
    private final String house;

    @Max(100)
    private final String flat;
}
