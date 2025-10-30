package ru.yandex.practicum.warehouse.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
public class CachedProduct implements ProductInfo {
    private UUID productId;
    private boolean fragile;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal depth;
    private BigDecimal weight;
    private int quantity;
}
