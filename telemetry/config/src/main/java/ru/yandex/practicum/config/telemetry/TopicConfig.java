package ru.yandex.practicum.config.telemetry;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "topics")
public class TopicConfig {
    private String sensors;
    private String snapshots;
    private String hubs;
}
