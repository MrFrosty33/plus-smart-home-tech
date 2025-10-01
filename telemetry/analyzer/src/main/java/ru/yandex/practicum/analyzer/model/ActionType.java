package ru.yandex.practicum.analyzer.model;

public enum ActionType {
    ACTIVATE,
    DEACTIVATE,
    INVERSE,
    SET_VALUE;

    public static ActionType fromString(String value) {
        try {
            return ActionType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ActionType: unable to parse from: " + value);
        }
    }
}
