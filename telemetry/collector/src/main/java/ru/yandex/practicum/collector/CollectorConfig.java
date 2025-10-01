package ru.yandex.practicum.collector;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.avro.AvroModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.config.telemetry.TopicConfig;
import ru.yandex.practicum.config.telemetry.collector.KafkaProducerConfig;

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
        return new KafkaProducerConfig();
    }

    @Bean
    public TopicConfig topicConfig() {
        return new TopicConfig();
    }


}
