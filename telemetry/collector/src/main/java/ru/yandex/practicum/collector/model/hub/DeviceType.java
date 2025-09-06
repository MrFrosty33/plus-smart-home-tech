package ru.yandex.practicum.collector.model.hub;

import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

public enum DeviceType {
    MOTION_SENSOR,
    TEMPERATURE_SENSOR,
    LIGHT_SENSOR,
    CLIMATE_SENSOR,
    SWITCH_SENSOR;

    public static DeviceTypeAvro toAvro(DeviceType value) {
        return DeviceTypeAvro.valueOf(value.name());
    }
}
