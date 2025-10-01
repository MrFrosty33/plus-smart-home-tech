package ru.yandex.practicum.aggregator;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.avro.AvroModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.aggregator.cache.SharedSensorSnapshotsCache;
import ru.yandex.practicum.config.telemetry.TopicConfig;
import ru.yandex.practicum.config.telemetry.aggregator.KafkaProducerConfig;
import ru.yandex.practicum.config.telemetry.analyzer.KafkaHubEventConsumerConfig;
import ru.yandex.practicum.config.telemetry.analyzer.KafkaSensorSnapshotConsumerConfig;
import ru.yandex.practicum.util.OffsetsManager;

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
        return new KafkaProducerConfig();
    }

    @Bean
    public KafkaHubEventConsumerConfig kafkaSensorEventConsumerConfig() {
        return new KafkaHubEventConsumerConfig();
    }

    @Bean
    public KafkaSensorSnapshotConsumerConfig kafkaSensorSnapshotConsumerConfig() {
        return new KafkaSensorSnapshotConsumerConfig();
    }

    @Bean
    public TopicConfig topicConfig() {
        return new TopicConfig();
    }
}
