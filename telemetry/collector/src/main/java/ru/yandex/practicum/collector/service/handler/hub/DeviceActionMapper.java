package ru.yandex.practicum.collector.service.handler.hub;

import ru.yandex.practicum.collector.model.hub.ActionType;
import ru.yandex.practicum.collector.model.hub.DeviceAction;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

public class DeviceActionMapper {
    public static DeviceActionAvro mapToAvro(DeviceAction action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionType.toAvro(action.getType()))
                .setValue(action.getValue())
                .build();
    }

}
