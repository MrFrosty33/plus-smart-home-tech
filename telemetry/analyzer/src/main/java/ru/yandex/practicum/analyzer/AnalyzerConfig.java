package ru.yandex.practicum.analyzer;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.avro.AvroModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.util.OffsetsManager;

import java.util.Properties;

public class AnalyzerConfig {
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

    //todo тут и в AggregatorConfig можно добавить возможность ввода через переменные окружения
    public static Properties getHubEventConsumerProperties() {
        Properties config = new Properties();
        // для докера
        //config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:29092");

        // для локального запуска
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.VoidDeserializer");
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.kafka.serializer.HubEventDeserializer");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "telemetry-analyzer-hub-event-consumers-v1");

        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        config.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 3072000);
        config.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 307200);

        // Добавить в конфигурацию потребителя
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        return config;
    }

    public static Properties getSensorSnapshotConsumerProperties() {
        Properties config = new Properties();
        // для докера
        //config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:29092");

        // для локального запуска
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.VoidDeserializer");
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.kafka.serializer.SensorSnapshotDeserializer");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "telemetry-analyzer-sensor-snapshot-consumers-v1");

        //todo такие же настройки требуются для текущего проекта??
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        config.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 3072000);
        config.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 307200);

        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        return config;
    }
}
