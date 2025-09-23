package ru.yandex.practicum.collector.service.handler.hub;

import ru.yandex.practicum.collector.exception.UnknownEnumException;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

public class DeviceActionMapper {
    public static DeviceActionAvro mapToAvro(DeviceActionProto action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(mapToAvro(action.getType()))
                .setValue(action.getValue())
                .build();
    }

    public static ActionTypeAvro mapToAvro(ActionTypeProto type) {
        if (type == ActionTypeProto.UNRECOGNIZED) {
            throw new UnknownEnumException("Unknown ActionTypeProto value: " + type);
        }
        return ActionTypeAvro.valueOf(type.name());
    }

}
