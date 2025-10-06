package ru.yandex.practicum.config.telemetry.aggregator;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Data
@Component
@ConfigurationProperties(prefix = "kafka-aggregator.producer")
public class KafkaProducerConfig {
    private Properties properties;
}
