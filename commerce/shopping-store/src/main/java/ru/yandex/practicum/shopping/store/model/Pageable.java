package ru.yandex.practicum.shopping.store.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.List;

@Data
public class Pageable {
    @PositiveOrZero
    private final int page;

    @Min(1)
    private final int size;

    private final List<String> sort;
}
