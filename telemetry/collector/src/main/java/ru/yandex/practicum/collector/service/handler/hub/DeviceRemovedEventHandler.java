package ru.yandex.practicum.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Component
public class DeviceRemovedEventHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {
    public DeviceRemovedEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected DeviceRemovedEventAvro mapToAvro(HubEventProto event) {
        DeviceRemovedEventProto _event = event.getDeviceRemoved();
        return DeviceRemovedEventAvro.newBuilder()
                .setId(_event.getId())
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }
}
