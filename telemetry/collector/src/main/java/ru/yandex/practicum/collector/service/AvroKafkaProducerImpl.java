package ru.yandex.practicum.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
public class AvroKafkaProducerImpl implements AvroKafkaProducer {
    private final KafkaProducer<Void, SpecificRecordBase> producer;
    private final String className = this.getClass().getSimpleName();

    public AvroKafkaProducerImpl() {
        Properties config = new Properties();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:29092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.VoidSerializer");
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer");

        this.producer = new KafkaProducer<>(config);
    }

    public void sendAvro(String topic, SpecificRecordBase avroMessage) {
        ProducerRecord<Void, SpecificRecordBase> record = new ProducerRecord<>(topic, avroMessage);
        try {
            producer.send(record);
            log.trace("{}: sent Avro message to topic {}: {}", className, topic, avroMessage);
        } catch (KafkaException e) {
            log.warn("{}: failed to send Avro message: {} with topic: {}", className, e.getMessage(), topic);
            throw e;
        }
    }
}
