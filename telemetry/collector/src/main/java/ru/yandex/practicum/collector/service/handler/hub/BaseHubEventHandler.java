package ru.yandex.practicum.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    protected final AvroKafkaProducer producer;
    private final String className = this.getClass().getSimpleName();

    protected abstract T mapToAvro(HubEventProto event);

    @Override
    public void handle(String topic, HubEventProto event) {
        T payload = mapToAvro(event);

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(
                        event.getTimestamp().getSeconds(),
                        event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();

        producer.sendAvro(topic, eventAvro);
        log.info("{}: processed payload: {} and sent eventAvro to kafka: {}", className, payload, eventAvro);
    }
}
