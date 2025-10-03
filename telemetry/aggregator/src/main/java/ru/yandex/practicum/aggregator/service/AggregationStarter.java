package ru.yandex.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.kafka.SensorEventConsumer;
import ru.yandex.practicum.aggregator.kafka.SensorSnapshotConsumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class AggregationStarter implements CommandLineRunner, AutoCloseable {
    private final SensorEventConsumer sensorEventConsumer;
    private final SensorSnapshotConsumer sensorSnapshotConsumer;
    private final String className = this.getClass().getSimpleName();

    @Override
    public void run(String... args) {
        try {
            Thread snapshotThread = new Thread(sensorSnapshotConsumer);
            snapshotThread.start();
            log.info("{}: sensorSnapshotConsumer thread started", className);

            Thread eventThread = new Thread(sensorEventConsumer);
            eventThread.start();
            log.info("{}: sensorEventConsumer thread started", className);

            snapshotThread.join();
            eventThread.join();
        } catch (Exception e) {
            log.error("{}: error acquired in run()", className, e);
        }
    }

    @Override
    public void close() throws Exception {
        log.trace("{}: closing resources", className);
        sensorEventConsumer.close();
        sensorSnapshotConsumer.close();
        log.trace("{}: resources closed", className);
    }
}