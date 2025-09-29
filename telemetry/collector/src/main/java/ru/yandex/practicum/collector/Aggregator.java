package ru.yandex.practicum.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.collector.service.AggregationStarter;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Aggregator {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Aggregator.class, args);

        AggregationStarter aggregator = context.getBean(AggregationStarter.class);
        String sensorsTopic = context.getEnvironment().getProperty("SENSORS_TOPIC");
        String snapshotsTopic = context.getEnvironment().getProperty("SNAPSHOTS_TOPIC");

        aggregator.start(sensorsTopic, snapshotsTopic);
    }
}