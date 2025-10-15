package ru.yandex.practicum.aggregator.kafka;

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
import ru.yandex.practicum.aggregator.cache.SharedSensorSnapshotsCache;
import ru.yandex.practicum.aggregator.config.KafkaSensorSnapshotConsumerConfig;
import ru.yandex.practicum.aggregator.config.TopicConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.util.ConsumerRecordDTO;
import ru.yandex.practicum.util.OffsetsManager;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SensorSnapshotConsumer implements Runnable, AutoCloseable {
    private final KafkaConsumer<Void, SpecificRecordBase> sensorSnapshotConsumer;
    private final Map<TopicPartition, OffsetAndMetadata> snapshotConsumerOffsets = new HashMap<>();

    private final SharedSensorSnapshotsCache sensorSnapshotsCache;

    private final String snapshotsTopic;
    private final OffsetsManager offsetsManager;

    private final JsonMapper jsonMapper;
    private final String className = SensorSnapshotConsumer.class.getSimpleName();

    private volatile boolean running = true;

    public SensorSnapshotConsumer(SharedSensorSnapshotsCache sensorSnapshotsCache,
                                  OffsetsManager offsetsManager,
                                  JsonMapper jsonMapper,
                                  KafkaSensorSnapshotConsumerConfig snapshotConsumerConfig,
                                  TopicConfig topics) {
        this.sensorSnapshotConsumer = new KafkaConsumer<>(snapshotConsumerConfig.getProperties());
        this.sensorSnapshotsCache = sensorSnapshotsCache;
        this.snapshotsTopic = topics.getSnapshots();
        this.offsetsManager = offsetsManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void run() {
        sensorSnapshotConsumer.subscribe(Collections.singletonList(snapshotsTopic));
        log.trace("{}: subscribed to topic {}", className, snapshotsTopic);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.trace("{}: shutdown hook triggered", className);
            running = false;
            sensorSnapshotConsumer.wakeup();
        }));

        try {
            while (running) {

                ConsumerRecords<Void, SpecificRecordBase> records = sensorSnapshotConsumer.poll(Duration.ofMillis(1_000));
                log.trace("{}: successfully polled {} records", className, records.count());
                int count = 0;

                for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                    log.trace("{}: processing record: {}", className,
                            jsonMapper.writeValueAsString(new ConsumerRecordDTO(record)));
                    SensorsSnapshotAvro snapshot = (SensorsSnapshotAvro) record.value();
                    sensorSnapshotsCache.put(snapshot.getHubId(), snapshot);
                    log.trace("{}: put to cache snapshot: {}",
                            className, jsonMapper.writeValueAsString(snapshot));
                    offsetsManager.manageOffsets(count, record, sensorSnapshotConsumer, snapshotConsumerOffsets);
                    count++;
                }

                sensorSnapshotConsumer.commitAsync();
            }
        } catch (WakeupException e) {
            if (running) {
                log.warn("{}: unexpected wakeup exception", className, e);
            } else {
                log.info("{}: woken up for shutdown", className);
            }
        } catch (Exception e) {
            log.error("{}: error acquired during processing of sensorSnapshots. Exception: {}",
                    className, e, e);
        } finally {
            log.info("{}: closing sensorSnapshotConsumer", className);
            sensorSnapshotConsumer.commitSync(snapshotConsumerOffsets);
            sensorSnapshotConsumer.close(Duration.ofSeconds(10));
            log.info("{}: finished", className);
        }
    }


    @Override
    public void close() {
        try {
            sensorSnapshotConsumer.wakeup();
        } catch (Exception e) {
            log.error("{}: error acquired during closing sensorSnapshotConsumer. Exception: {}",
                    className, e, e);
        }

    }
}
