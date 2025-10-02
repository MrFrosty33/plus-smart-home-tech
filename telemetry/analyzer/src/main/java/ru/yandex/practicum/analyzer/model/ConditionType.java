package ru.yandex.practicum.analyzer.model;

public enum ConditionType {
    MOTION,
    LUMINOSITY,
    SWITCH,
    TEMPERATURE,
    CO2LEVEL,
    HUMIDITY;

    public static ConditionType fromString(String value) {
        try {
            return ConditionType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ConditionType: unable to parse from: " + value);
        }
    }
}
