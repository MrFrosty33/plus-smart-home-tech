package ru.yandex.practicum.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.collector.model.hub.HubEventType;
import ru.yandex.practicum.collector.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        ScenarioAddedEvent _event = (ScenarioAddedEvent) event;
        return ScenarioAddedEventAvro.newBuilder()
                .setName(_event.getName())
                .setConditions(_event.getConditions().stream().map(ScenarioConditionMapper::mapToAvro).toList())
                .setActions(_event.getActions().stream().map(DeviceActionMapper::mapToAvro).toList())
                .build();
    }

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}