package ru.yandex.practicum.collector.model.hub;

import ru.yandex.practicum.kafka.telemetry.event.OperationTypeAvro;

public enum OperationType {
    EQUALS,
    GREATER_THAN,
    LOWER_THAN;

    public static OperationTypeAvro toAvro(OperationType operationType) {
        return OperationTypeAvro.valueOf(operationType.name());
    }
}
