package ru.yandex.practicum.analyzer.model.mapper;

import ru.yandex.practicum.analyzer.model.Action;
import ru.yandex.practicum.analyzer.model.dto.ActionDto;

public class ActionMapper {
    public static ActionDto toDto(Action entity) {
        ActionDto dto = new ActionDto();
        dto.setType(entity.getType());
        dto.setValue(entity.getValue());
        return dto;
    }
}
