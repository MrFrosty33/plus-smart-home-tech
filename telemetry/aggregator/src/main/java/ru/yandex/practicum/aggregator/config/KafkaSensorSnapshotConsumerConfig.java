package ru.yandex.practicum.aggregator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Data
@Component
@ConfigurationProperties(prefix = "kafka-aggregator.sensor-snapshot-consumer")
public class KafkaSensorSnapshotConsumerConfig {
    private Properties properties;
}
