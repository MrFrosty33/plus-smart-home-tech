package ru.yandex.practicum.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.collector.model.hub.HubEventType;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventTypeAvro;

@Component
public class DeviceRemovedEventHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {
    public DeviceRemovedEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected DeviceRemovedEventAvro mapToAvro(HubEvent event) {
        DeviceRemovedEvent _event = (DeviceRemovedEvent) event;
        return DeviceRemovedEventAvro.newBuilder()
                .setHubId(_event.getHubId())
                .setTimestamp(_event.getTimestamp())
                .setId(_event.getId())
                .setType(HubEventTypeAvro.DEVICE_REMOVED)
                .build();
    }

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
