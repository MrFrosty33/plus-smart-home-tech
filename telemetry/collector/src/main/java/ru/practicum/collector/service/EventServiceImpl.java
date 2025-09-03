package ru.practicum.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.sensor.SensorEvent;

import java.util.Properties;

@Service
@Slf4j
public class EventServiceImpl implements EventService {
    private final String className = this.getClass().getSimpleName();
    private final KafkaProducer<Void, SensorEvent> sensorProducer;
    private final KafkaProducer<Void, HubEvent> hubProducer;

    private final String sensorTopic;
    private final String hubTopic;


    public EventServiceImpl(@Value("${SENSORS_TOPIC}") String sensorTopic, @Value("${HUBS_TOPIC}") String hubTopic) {
        this.sensorTopic = sensorTopic;
        this.hubTopic = hubTopic;
        Properties sensorConfig = new Properties();
        Properties hubConfig = new Properties();

        sensorConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:29092");
        sensorConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.VoidSerializer");
        sensorConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "ru.practicum.collector.serialization.SensorEventSerializer");
        this.sensorProducer = new KafkaProducer<>(sensorConfig);

        hubConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:29092");
        hubConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.VoidSerializer");
        hubConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "ru.practicum.collector.serialization.HubEventSerializer");
        this.hubProducer = new KafkaProducer<>(hubConfig);
    }

    @Override
    public void produceSensorData(SensorEvent sensorEvent) {
        ProducerRecord<Void, SensorEvent> record = new ProducerRecord<>(sensorTopic, sensorEvent);
        try {
            sensorProducer.send(record);
            log.trace("{}: sent SensorEvent to topic {}: {}", className, sensorTopic, sensorEvent);
        } catch (KafkaException e) {
            log.warn("{}: failed to send SensorEvent: {} with topic: {}", className, e.getMessage(), sensorTopic);
            throw e;
        }
    }

    @Override
    public void produceHubData(HubEvent hubEvent) {
        ProducerRecord<Void, HubEvent> record = new ProducerRecord<>(hubTopic, hubEvent);
        try {
            hubProducer.send(record);
            log.trace("{}: sent HubEvent to topic {}: {}", className, hubTopic, hubEvent);
        } catch (KafkaException e) {
            log.warn("{}: failed to send HubEvent: {} with topic: {}", className, e.getMessage(), hubTopic);
            throw e;
        }
    }
}
