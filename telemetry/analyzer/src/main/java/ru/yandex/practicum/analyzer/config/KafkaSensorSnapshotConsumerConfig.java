package ru.yandex.practicum.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Data
@Component
@ConfigurationProperties(prefix = "kafka-analyzer.sensor-snapshot-consumer")
public class KafkaSensorSnapshotConsumerConfig {
    public Properties properties;
}
