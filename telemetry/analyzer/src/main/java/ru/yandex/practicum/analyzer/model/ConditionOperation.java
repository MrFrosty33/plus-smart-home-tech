package ru.yandex.practicum.analyzer.model;

public enum ConditionOperation {
    EQUALS,
    GREATER_THAN,
    LOWER_THAN;

    public static ConditionOperation fromString(String value) {
        try {
            return ConditionOperation.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ConditionOperation: unable to parse from: " + value);
        }
    }
}
