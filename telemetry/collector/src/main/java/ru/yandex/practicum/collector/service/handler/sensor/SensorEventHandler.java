package ru.yandex.practicum.collector.service.handler.sensor;

import ru.yandex.practicum.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getType();

    void handle(String topic, SensorEvent event);
}
