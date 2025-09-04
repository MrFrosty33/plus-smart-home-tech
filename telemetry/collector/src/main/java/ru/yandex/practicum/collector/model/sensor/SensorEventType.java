package ru.yandex.practicum.collector.model.sensor;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventTypeAvro;

public enum SensorEventType {
    MOTION_SENSOR_EVENT,
    TEMPERATURE_SENSOR_EVENT,
    LIGHT_SENSOR_EVENT,
    CLIMATE_SENSOR_EVENT,
    SWITCH_SENSOR_EVENT;

    public static SensorEventTypeAvro toAvro(SensorEventType type) {
        return SensorEventTypeAvro.valueOf(type.name());
    }
}
