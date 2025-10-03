package ru.yandex.practicum.analyzer.model.mapper;

import ru.yandex.practicum.analyzer.model.Condition;
import ru.yandex.practicum.analyzer.model.dto.ConditionDto;

public class ConditionMapper {
    public static ConditionDto toDto(Condition entity) {
        ConditionDto dto = new ConditionDto();
        dto.setType(entity.getType());
        dto.setOperation(entity.getOperation());
        dto.setValue(entity.getValue());
        return dto;
    }
}
