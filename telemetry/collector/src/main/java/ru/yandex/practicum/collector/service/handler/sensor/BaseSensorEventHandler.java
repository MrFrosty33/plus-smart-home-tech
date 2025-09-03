package ru.yandex.practicum.collector.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    protected final AvroKafkaProducer producer;

    protected abstract T mapToAvro(SensorEvent event);

    @Override
    public void handle(String topic, SensorEvent event) {
        T avroMessage = mapToAvro(event);
        producer.sendAvro(topic, avroMessage);
        log.info("Processed event: {}", event);
    }
}
