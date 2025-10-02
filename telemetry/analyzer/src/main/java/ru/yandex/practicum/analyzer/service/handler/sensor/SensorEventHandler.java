package ru.yandex.practicum.analyzer.service.handler.sensor;

import ru.yandex.practicum.analyzer.model.dto.ConditionDto;

public interface SensorEventHandler<T> {
    boolean processSensorCondition(T data, ConditionDto condition);

    boolean processOperation(int sensorValue, ConditionDto condition);

    Class<T> getHandledSensorClass();
}
