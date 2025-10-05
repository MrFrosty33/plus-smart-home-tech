package ru.yandex.practicum.aggregator;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.avro.AvroModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.aggregator.cache.SharedSensorSnapshotsCache;
import ru.yandex.practicum.config.telemetry.TopicConfig;
import ru.yandex.practicum.config.telemetry.aggregator.KafkaProducerConfig;
import ru.yandex.practicum.config.telemetry.aggregator.KafkaSensorEventConsumerConfig;
import ru.yandex.practicum.config.telemetry.aggregator.KafkaSensorSnapshotConsumerConfig;
import ru.yandex.practicum.util.OffsetsManager;

import java.util.Properties;

@Configuration
public class AggregatorConfig {
    @Bean
    public JsonMapper jsonMapper() {
        JsonMapper result = new JsonMapper();
        result.registerModule(new JavaTimeModule());
        result.registerModule(new AvroModule());
        return result;
    }

    @Bean
    public OffsetsManager offsetsManager() {
        return new OffsetsManager();
    }

    @Bean
    public SharedSensorSnapshotsCache sharedSensorSnapshotsCache() {
        return new SharedSensorSnapshotsCache();
    }

    @Bean
    public KafkaProducerConfig kafkaProducerConfig() {
        KafkaProducerConfig result = new KafkaProducerConfig();
        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");

        props.put("group.id", "telemetry-aggregator-producer-v1");

        props.put("key.serializer", "org.apache.kafka.common.serialization.VoidSerializer");
        props.put("value.serializer", "ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer");
        result.setProperties(props);
        return result;
//        return new KafkaProducerConfig(); // для IDE / docker
    }

    @Bean
    public KafkaSensorEventConsumerConfig kafkaSensorEventConsumerConfig() {
        KafkaSensorEventConsumerConfig result = new KafkaSensorEventConsumerConfig();
        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");

        props.put("group.id", "telemetry-aggregator-sensor-event-consumers-v1");

        props.put("key.deserializer", "org.apache.kafka.common.serialization.VoidDeserializer");
        props.put("value.deserializer", "ru.yandex.practicum.kafka.serializer.SensorEventDeserializer");

        props.put("max.poll.records", "100");
        props.put("fetch.max.bytes", "3072000");
        props.put("max.partition.fetch.bytes", "307200");

        props.put("auto.offset.reset", "latest");
        props.put("isolation.level", "read_committed");
        props.put("enable.auto.commit", "false");
        result.setProperties(props);
        return result;
//        return new KafkaSensorEventConsumerConfig(); // для IDE / docker
    }

    @Bean
    public KafkaSensorSnapshotConsumerConfig kafkaSensorSnapshotConsumerConfig() {
        KafkaSensorSnapshotConsumerConfig result = new KafkaSensorSnapshotConsumerConfig();
        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");

        props.put("group.id", "telemetry-aggregator-sensor-snapshot-consumers-v1");

        props.put("key.deserializer", "org.apache.kafka.common.serialization.VoidDeserializer");
        props.put("value.deserializer", "ru.yandex.practicum.kafka.serializer.SensorSnapshotDeserializer");

        props.put("max.poll.records", "100");
        props.put("fetch.max.bytes", "3072000");
        props.put("max.partition.fetch.bytes", "307200");

        props.put("auto.offset.reset", "latest");
        props.put("isolation.level", "read_committed");
        props.put("enable.auto.commit", "false");
        result.setProperties(props);
        return result;
//        return new KafkaSensorSnapshotConsumerConfig(); // для IDE / docker
    }

    @Bean
    public TopicConfig topicConfig() {
        TopicConfig result = new TopicConfig();
        result.setSensors("telemetry.sensors.v1");
        result.setSnapshots("telemetry.snapshots.v1");
        result.setHubs("telemetry.hubs.v1");
        return result;
//        return new TopicConfig(); // для IDE / docker
    }
}
