package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.kafka.HubConsumer;
import ru.yandex.practicum.analyzer.kafka.SnapshotConsumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class AnalyzerStarter implements CommandLineRunner, AutoCloseable {
    private final HubConsumer hubConsumer;
    private final SnapshotConsumer snapshotConsumer;

    private final String className = this.getClass().getSimpleName();

    @Override
    public void run(String... args) {
        try {
            while (true) {
                Thread hubThread = new Thread(hubConsumer);
                hubThread.start();
                log.info("{}: hubConsumer thread started", className);

                Thread snapshotThread = new Thread(snapshotConsumer);
                snapshotThread.start();
                log.info("{}: snapshotConsumer thread started", className);

                hubThread.join();
                snapshotThread.join();
            }
        } catch (Exception e) {
            log.error("{}: error acquired in run()", className, e);
        }
    }

    @Override
    public void close() throws Exception {
        log.trace("{}: closing resources", className);
        hubConsumer.close();
        snapshotConsumer.close();
        log.trace("{}: resources closed", className);
    }
}
