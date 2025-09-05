package ru.yandex.practicum.collector.model.hub;

import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

public enum ConditionOperation {
    EQUALS,
    GREATER_THAN,
    LOWER_THAN;

    public static ConditionOperationAvro toAvro(ConditionOperation conditionOperation) {
        return ConditionOperationAvro.valueOf(conditionOperation.name());
    }
}
