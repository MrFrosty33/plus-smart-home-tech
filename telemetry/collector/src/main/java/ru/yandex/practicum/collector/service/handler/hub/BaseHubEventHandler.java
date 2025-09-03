package ru.yandex.practicum.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    protected final AvroKafkaProducer producer;

    protected abstract T mapToAvro(HubEvent event);

    @Override
    public void handle(String topic, HubEvent event) {
        T avroMessage = mapToAvro(event);
        producer.sendAvro(topic, avroMessage);
        log.info("Processed event: {}", event);
    }
}
