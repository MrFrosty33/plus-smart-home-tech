package ru.yandex.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.kafka.EventConsumer;
import ru.yandex.practicum.aggregator.kafka.SnapshotConsumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class AggregationStarter implements CommandLineRunner, AutoCloseable {
    private final EventConsumer eventConsumer;
    private final SnapshotConsumer snapshotConsumer;
    private final String className = this.getClass().getSimpleName();

    @Override
    public void run(String... args) {
        try {
            Thread snapshotThread = new Thread(snapshotConsumer);
            snapshotThread.start();
            log.info("{}: snapshotConsumer thread started", className);

            Thread eventThread = new Thread(eventConsumer);
            eventThread.start();
            log.info("{}: eventConsumer thread started", className);

            snapshotThread.join();
            eventThread.join();
        } catch (Exception e) {
            log.error("{}: error acquired in run()", className, e);
        }
    }

    @Override
    public void close() throws Exception {
        log.trace("{}: closing resources", className);
        eventConsumer.close();
        snapshotConsumer.close();
        log.trace("{}: resources closed", className);
    }
}