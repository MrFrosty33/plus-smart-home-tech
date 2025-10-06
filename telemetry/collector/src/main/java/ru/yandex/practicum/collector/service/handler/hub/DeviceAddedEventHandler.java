package ru.yandex.practicum.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.exception.UnknownEnumException;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
    public DeviceAddedEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEventProto event) {
        DeviceAddedEventProto _event = event.getDeviceAdded();

        return DeviceAddedEventAvro.newBuilder()
                .setId(_event.getId())
                .setType(mapToAvro(_event.getType()))
                .build();
    }

    public static DeviceTypeAvro mapToAvro(DeviceTypeProto type) {
        if (type == DeviceTypeProto.UNRECOGNIZED) {
            throw new UnknownEnumException("Unknown DeviceTypeProto value: " + type);
        }
        return DeviceTypeAvro.valueOf(type.name());
    }

    @Override
    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }
}
