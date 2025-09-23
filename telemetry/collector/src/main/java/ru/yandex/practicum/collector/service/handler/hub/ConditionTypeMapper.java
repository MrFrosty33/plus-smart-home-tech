package ru.yandex.practicum.collector.service.handler.hub;

import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

public class ConditionTypeMapper {
    public static ScenarioConditionAvro mapToAvro(ScenarioConditionProto condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(mapToAvro(condition.getType()))
                .setValue(getValue(condition))
                .build();
    }

    public static ConditionTypeAvro mapToAvro(ConditionTypeProto type) {
        if (type == ConditionTypeProto.UNRECOGNIZED) {
            throw new IllegalArgumentException("Unknown ConditionTypeProto value: " + type);
        }
        return ConditionTypeAvro.valueOf(type.name());
    }

    private static Object getValue(ScenarioConditionProto condition) {
        return switch (condition.getValueCase()) {
            case BOOL_VALUE -> condition.getBoolValue();
            case INT_VALUE -> condition.getIntValue();
            case VALUE_NOT_SET -> null;
        };
    }
}
