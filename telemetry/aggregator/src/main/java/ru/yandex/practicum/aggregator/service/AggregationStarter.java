package ru.yandex.practicum.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.service.kafka.EventConsumer;
import ru.yandex.practicum.aggregator.service.kafka.SnapshotConsumer;

@Component
@Slf4j
public class AggregationStarter implements AutoCloseable {
    private final EventConsumer eventConsumer;
    private final SnapshotConsumer snapshotConsumer;
    private final String className = this.getClass().getSimpleName();

    public AggregationStarter(EventConsumer eventConsumer, SnapshotConsumer snapshotConsumer) {
        this.eventConsumer = eventConsumer;
        this.snapshotConsumer = snapshotConsumer;
    }

    public void start() {
        try {
            Thread snapshotThread = new Thread(snapshotConsumer::start);
            snapshotThread.start();
            log.info("{}: snapshotConsumer thread started", className);

            Thread eventThread = new Thread(eventConsumer::start);
            eventThread.start();
            log.info("{}: eventConsumer thread started", className);

            snapshotThread.join();
            eventThread.join();
        } catch (Exception e) {
            log.error("{}: error acquired in start()", className, e);
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