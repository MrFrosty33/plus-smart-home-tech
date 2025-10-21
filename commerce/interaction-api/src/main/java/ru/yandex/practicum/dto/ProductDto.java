package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductDto {
    @Max(36)
    private final String productId;

    @Max(100)
    @NotBlank
    private final String productName;

    @Max(1000)
    @NotBlank
    private final String description;

    @Max(1000)
    private final String imageSrc;

    private final QuantityState quantityState;
    private final ProductState productState;
    private final ProductCategory productCategory;

    @Min(1)
    private final double price;

}
