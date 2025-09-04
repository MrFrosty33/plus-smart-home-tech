package ru.yandex.practicum.collector.service.handler.hub;

import ru.yandex.practicum.collector.model.hub.ConditionType;
import ru.yandex.practicum.collector.model.hub.OperationType;
import ru.yandex.practicum.collector.model.hub.ScenarioCondition;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

public class ScenarioConditionMapper {
    public static ScenarioConditionAvro mapToAvro(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionType.toAvro(condition.getType()))
                .setOperation(OperationType.toAvro(condition.getOperation()))
                .setValue(condition.getValue())
                .build();
    }
}
