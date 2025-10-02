package ru.yandex.practicum.analyzer.model.dto;

import lombok.Data;
import ru.yandex.practicum.analyzer.model.ConditionOperation;
import ru.yandex.practicum.analyzer.model.ConditionType;

@Data
public class ConditionDto {
    private ConditionType type;
    private ConditionOperation operation;
    private Integer value;
}
