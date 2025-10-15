package ru.yandex.practicum.analyzer.kafka;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.config.KafkaSensorSnapshotConsumerConfig;
import ru.yandex.practicum.analyzer.config.TopicConfig;
import ru.yandex.practicum.analyzer.service.AnalyzerService;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.util.ConsumerRecordDTO;
import ru.yandex.practicum.util.OffsetsManager;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SnapshotConsumer implements Runnable, AutoCloseable {
    private final KafkaConsumer<Void, SpecificRecordBase> snapshotConsumer;
    private final Map<TopicPartition, OffsetAndMetadata> snapshotConsumerOffsets = new HashMap<>();

    private final AnalyzerService analyzerService;

    private final String snapshotsTopic;
    private final OffsetsManager offsetsManager;

    private final JsonMapper jsonMapper;
    private final String className = SnapshotConsumer.class.getSimpleName();

    private volatile boolean running = true;

    public SnapshotConsumer(AnalyzerService analyzerService,
                            OffsetsManager offsetsManager,
                            JsonMapper jsonMapper,
                            KafkaSensorSnapshotConsumerConfig sensorSnapshotConsumerConfig,
                            TopicConfig topics) {
        this.snapshotConsumer = new KafkaConsumer<>(sensorSnapshotConsumerConfig.getProperties());
        this.analyzerService = analyzerService;
        this.snapshotsTopic = topics.getSnapshots();
        this.offsetsManager = offsetsManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void run() {
        snapshotConsumer.subscribe(Collections.singletonList(snapshotsTopic));
        log.trace("{}: subscribed to topic {}", className, snapshotsTopic);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.trace("{}: shutdown hook triggered", className);
            running = false;
            snapshotConsumer.wakeup();
        }));

        try {
            while (running) {

                ConsumerRecords<Void, SpecificRecordBase> records = snapshotConsumer.poll(Duration.ofMillis(1_000));
                log.trace("{}: snapshotConsumer successfully polled {} records", className, records.count());
                int count = 0;

                for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                    log.trace("{}: redirecting to analyzerService for processing record: {}", className,
                            jsonMapper.writeValueAsString(new ConsumerRecordDTO(record)));

                    SensorsSnapshotAvro snapshot = (SensorsSnapshotAvro) record.value();
                    analyzerService.processSensorSnapshot(snapshot);

                    offsetsManager.manageOffsets(count, record, snapshotConsumer, snapshotConsumerOffsets);
                    count++;
                }

                snapshotConsumer.commitAsync();
            }
        } catch (WakeupException e) {
            if (running) {
                log.warn("{}: unexpected wakeup exception", className, e);
            } else {
                log.info("{}: woken up for shutdown", className);
            }
        } catch (Exception e) {
            log.error("{}: error acquired during processing of sensorSnapshots in snapshotConsumerThread. Exception: {}",
                    className, e, e);
        } finally {
            log.info("{}: closing snapshotConsumer", className);
            snapshotConsumer.commitSync(snapshotConsumerOffsets);
            snapshotConsumer.close(Duration.ofSeconds(10));
            log.info("{}: finished", className);
        }
    }


    @Override
    public void close() {
        try {
            snapshotConsumer.wakeup();
        } catch (Exception e) {
            log.error("{}: error acquired during closing snapshotConsumer. Exception: {}",
                    className, e, e);
        }

    }
}
