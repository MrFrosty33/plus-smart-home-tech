package ru.yandex.practicum.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.collector.model.hub.DeviceType;
import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.collector.model.hub.HubEventType;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;

import static ru.yandex.practicum.collector.model.hub.HubEventType.DEVICE_ADDED;

@Component
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
    public DeviceAddedEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEvent event) {
        DeviceAddedEvent _event = (DeviceAddedEvent) event;
//        return DeviceAddedEventAvro.newBuilder()
//                .setHubId(_event.getHubId())
//                .setTimestamp(_event.getTimestamp())
//                .setId(_event.getId())
//                .setDeviceType(DeviceType.toAvro(_event.getDeviceType()))
//                .setType(HubEventTypeAvro.DEVICE_ADDED)
//                .build();

        return DeviceAddedEventAvro.newBuilder()
                .setId(_event.getId())
                .setType(DeviceType.toAvro(_event.getDeviceType()))
                .build();
    }

    @Override
    public HubEventType getType() {
        return DEVICE_ADDED;
    }
}
