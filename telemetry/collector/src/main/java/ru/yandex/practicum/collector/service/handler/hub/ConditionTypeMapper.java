package ru.yandex.practicum.collector.service.handler.hub;

import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

public class ConditionTypeMapper {
    public static ScenarioConditionAvro mapToAvro(ScenarioConditionProto condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(mapToAvro(condition.getType()))
                .setOperation(mapToAvro(condition.getOperation()))
                .setValue(getValue(condition))
                .build();
    }

    public static ConditionTypeAvro mapToAvro(ConditionTypeProto type) {
        if (type == ConditionTypeProto.UNRECOGNIZED) {
            throw new IllegalArgumentException("Unknown ConditionTypeProto value: " + type);
        }
        return ConditionTypeAvro.valueOf(type.name());
    }

    public static ConditionOperationAvro mapToAvro(ConditionOperationProto operation) {
        if (operation == ConditionOperationProto.UNRECOGNIZED) {
            throw new IllegalArgumentException("Unknown ConditionOperationProto value: " + operation);
        }
        return ConditionOperationAvro.valueOf(operation.name());
    }

    private static Object getValue(ScenarioConditionProto condition) {
        return switch (condition.getValueCase()) {
            case BOOL_VALUE -> condition.getBoolValue();
            case INT_VALUE -> condition.getIntValue();
            case VALUE_NOT_SET -> null;
        };
    }
}
