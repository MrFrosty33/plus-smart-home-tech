package ru.practicum.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.stereotype.Service;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.sensor.SensorEvent;

import java.util.Properties;

@Service
@Slf4j
public class EventServiceImpl {
    private final String className = this.getClass().getSimpleName();
    private final KafkaProducer<Void, SensorEvent> sensorProducer;
    private final KafkaProducer<Void, HubEvent> hubProducer;


    public EventServiceImpl(KafkaProducer<Void, SensorEvent> sensorProducer, KafkaProducer<Void, HubEvent> hubProducer) {
        Properties sensorConfig = new Properties();
        sensorConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        sensorConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.VoidSerializer");
        sensorConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "ru.practicum.collector.serialization.SensorEventSerializer");
        this.sensorProducer = new KafkaProducer<>(sensorConfig);
    }
}
