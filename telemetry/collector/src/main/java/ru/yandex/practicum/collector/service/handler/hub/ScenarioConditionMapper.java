package ru.yandex.practicum.collector.service.handler.hub;

import ru.yandex.practicum.collector.model.hub.ConditionType;
import ru.yandex.practicum.collector.model.hub.ConditionOperation;
import ru.yandex.practicum.collector.model.hub.ScenarioCondition;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

public class ScenarioConditionMapper {
    public static ScenarioConditionAvro mapToAvro(ScenarioCondition condition) {
        ScenarioConditionAvro result = ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionType.toAvro(condition.getType()))
                .setOperation(ConditionOperation.toAvro(condition.getOperation()))
                .build();

        if (condition.getValue() instanceof Integer) {
            result.setValue((Integer) condition.getValue());
        } else if (condition.getValue() instanceof Boolean) {
            result.setValue((Boolean) condition.getValue());
        } else if (condition.getValue() == null) {
            result.setValue(null);
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + condition.getValue().getClass());
        }

        return result;
    }
}
