package ru.yandex.practicum.collector.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    protected final AvroKafkaProducer producer;
    private final String className = this.getClass().getSimpleName();

    protected abstract T mapToAvro(SensorEventProto event);

    @Override
    public void handle(String topic, SensorEventProto event) {
        T payload = mapToAvro(event);

        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
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
