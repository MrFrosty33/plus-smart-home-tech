package ru.yandex.practicum.collector;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.avro.AvroModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.config.telemetry.TopicConfig;
import ru.yandex.practicum.config.telemetry.collector.KafkaProducerConfig;

import java.util.Properties;

@Configuration
public class CollectorConfig {

    @Bean
    public JsonMapper jsonMapper() {
        JsonMapper result = new JsonMapper();
        result.registerModule(new JavaTimeModule());
        result.registerModule(new AvroModule());
        result.registerModule(new ProtobufModule());
        return result;
    }

    @Bean
    public KafkaProducerConfig kafkaProducerConfig() {
        KafkaProducerConfig result = new KafkaProducerConfig();
        Properties props = new Properties();

        // для GitHub CI
        props.put("bootstrap.servers", "localhost:29092");

        props.put("group.id", "telemetry-collector-producer-v1");

        props.put("key.serializer", "org.apache.kafka.common.serialization.VoidSerializer");
        props.put("value.serializer", "ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer");
        result.setProperties(props);
        return result;
//        return new KafkaProducerConfig(); // для IDE / docker
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
