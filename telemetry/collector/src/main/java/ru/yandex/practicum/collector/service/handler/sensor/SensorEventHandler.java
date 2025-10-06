package ru.yandex.practicum.collector.service.handler.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getType();

    void handle(String topic, SensorEventProto event);
}
