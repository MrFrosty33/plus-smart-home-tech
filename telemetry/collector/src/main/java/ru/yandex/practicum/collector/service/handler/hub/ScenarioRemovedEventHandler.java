package ru.yandex.practicum.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.collector.model.hub.HubEventType;
import ru.yandex.practicum.collector.model.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {
    public ScenarioRemovedEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected ScenarioRemovedEventAvro mapToAvro(HubEvent event) {
        ScenarioRemovedEvent _event = (ScenarioRemovedEvent) event;
        return ScenarioRemovedEventAvro.newBuilder()
                .setHubId(_event.getHubId())
                .setTimestamp(_event.getTimestamp())
                .setName(_event.getName())
                .setType(HubEventTypeAvro.SCENARIO_REMOVED)
                .build();
    }

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
