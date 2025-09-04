package ru.yandex.practicum.collector.model.hub;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

public enum ConditionType {
    MOTION,
    LUMINOSITY,
    SWITCH,
    TEMPERATURE,
    CO2LEVEL,
    HUMIDITY;

    public static ConditionTypeAvro toAvro(ConditionType conditionType) {
        return ConditionTypeAvro.valueOf(conditionType.name());
    }
}
