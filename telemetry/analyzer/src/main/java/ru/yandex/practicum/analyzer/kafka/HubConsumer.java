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
import ru.yandex.practicum.analyzer.config.KafkaHubEventConsumerConfig;
import ru.yandex.practicum.analyzer.config.TopicConfig;
import ru.yandex.practicum.analyzer.service.AnalyzerService;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.util.ConsumerRecordDTO;
import ru.yandex.practicum.util.OffsetsManager;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class HubConsumer implements Runnable, AutoCloseable {
    private final KafkaConsumer<Void, SpecificRecordBase> hubConsumer;
    private final Map<TopicPartition, OffsetAndMetadata> hubConsumerOffsets = new HashMap<>();

    private final AnalyzerService analyzerService;

    private final String hubsTopics;
    private final OffsetsManager offsetsManager;

    private final JsonMapper jsonMapper;
    private final String className = HubConsumer.class.getSimpleName();

    private volatile boolean running = true;

    public HubConsumer(AnalyzerService analyzerService,
                       OffsetsManager offsetsManager,
                       JsonMapper jsonMapper,
                       KafkaHubEventConsumerConfig hubEventConsumerConfig,
                       TopicConfig topicConfig) {
        this.hubConsumer = new KafkaConsumer<>(hubEventConsumerConfig.getProperties());
        this.analyzerService = analyzerService;
        this.hubsTopics = topicConfig.getHubs();
        this.offsetsManager = offsetsManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void run() {
        hubConsumer.subscribe(Collections.singletonList(hubsTopics));
        log.trace("{}: subscribed to topic {}", className, hubsTopics);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.trace("{}: shutdown hook triggered", className);
            running = false;
            hubConsumer.wakeup();
        }));

        try {
            while (running) {

                ConsumerRecords<Void, SpecificRecordBase> records = hubConsumer.poll(Duration.ofMillis(1_000));
                log.trace("{}: hubConsumer successfully polled {} records", className, records.count());
                int count = 0;

                for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                    log.trace("{}: redirecting to analyzerService for processing record: {}", className,
                            jsonMapper.writeValueAsString(new ConsumerRecordDTO(record)));

                    HubEventAvro hubEvent = (HubEventAvro) record.value();
                    analyzerService.processHubEvent(hubEvent);

                    offsetsManager.manageOffsets(count, record, hubConsumer, hubConsumerOffsets);
                    count++;
                }

                hubConsumer.commitAsync();
            }
        } catch (WakeupException e) {
            if (running) {
                log.warn("{}: unexpected wakeup exception", className, e);
            } else {
                log.info("{}: woken up for shutdown", className);
            }
        } catch (Exception e) {
            log.error("{}: error acquired during processing of sensorSnapshots in hubConsumerThread. Exception: {}",
                    className, e, e);
        } finally {
            log.info("{}: closing hubConsumer", className);
            hubConsumer.commitSync(hubConsumerOffsets);
            hubConsumer.close(Duration.ofSeconds(10));
            log.info("{}: finished", className);
        }
    }


    @Override
    public void close() {
        try {
            hubConsumer.wakeup();
        } catch (Exception e) {
            log.error("{}: error acquired during closing hubConsumer. Exception: {}",
                    className, e, e);
        }

    }
}
