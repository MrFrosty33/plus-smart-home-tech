package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ProductDto {
    private final UUID productId;

    @Length(max = 100)
    @NotBlank
    private final String productName;

    @Length(max = 1000)
    @NotBlank
    private final String description;

    @Length(max = 1000)
    private final String imageSrc;

    private final QuantityState quantityState;
    private final ProductState productState;
    private final ProductCategory productCategory;

    @Min(1)
    private final BigDecimal price;

}
