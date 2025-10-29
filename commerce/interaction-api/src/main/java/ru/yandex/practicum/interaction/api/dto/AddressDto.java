package ru.yandex.practicum.interaction.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@Builder
public class AddressDto {
    @Length(max = 100)
    private final String country;

    @Length(max = 100)
    private final String city;

    @Length(max = 1000)
    private final String street;

    @Length(max = 1000)
    private final String house;

    @Length(max = 1000)
    private final String flat;
}
