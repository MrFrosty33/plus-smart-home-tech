package ru.yandex.practicum.collector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.CollectorConfig;
import ru.yandex.practicum.collector.exception.UnknownEnumException;

import java.time.Duration;

@Component
@Slf4j
public class AvroKafkaProducerImpl implements AvroKafkaProducer, AutoCloseable {
    private final KafkaProducer<Void, SpecificRecordBase> producer;
    private final String className = this.getClass().getSimpleName();
    private final JsonMapper jsonMapper;

    public AvroKafkaProducerImpl(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
        this.producer = new KafkaProducer<>(CollectorConfig.getProducerProperties());
    }

    public void sendAvro(String topic, SpecificRecordBase avroMessage) {
        ProducerRecord<Void, SpecificRecordBase> record = new ProducerRecord<>(topic, avroMessage);
        try {
            producer.send(record);
            log.trace("{}: sent Avro message to topic {}: {}", className, topic, jsonMapper.writeValueAsString(avroMessage));
        } catch (KafkaException e) {
            log.warn("{}: failed to send Avro message: {} with topic: {}", className, e.getMessage(), topic);
            throw e;
        } catch (JsonProcessingException e) {
            log.warn("{}: Error processing avroMessage JSON: {}", className, e.getMessage());
            throw new UnknownEnumException("Error processing avroMessage JSON");
        }
    }

    @Override
    public void close() throws Exception {
        producer.flush();
        producer.close(Duration.ofSeconds(10));
    }
}
