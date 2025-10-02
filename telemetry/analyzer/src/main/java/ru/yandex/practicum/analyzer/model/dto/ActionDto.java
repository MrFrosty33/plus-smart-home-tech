package ru.yandex.practicum.analyzer.model.dto;

import lombok.Data;
import ru.yandex.practicum.analyzer.model.ActionType;

@Data
public class ActionDto {
    private ActionType type;
    private Integer value;
}
