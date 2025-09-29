package ru.yandex.practicum.collector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.CollectorConfig;
import ru.yandex.practicum.kafka.serializer.SensorEventDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class AggregationStarter implements AutoCloseable {
    private final KafkaConsumer<Void, SpecificRecordBase> eventConsumer;
    private final Map<TopicPartition, OffsetAndMetadata> eventConsumerOffsets = new HashMap<>();

    private final KafkaConsumer<Void, SpecificRecordBase> snapshotConsumer;
    private final KafkaProducer<Void, SpecificRecordBase> snapshotProducer;
    private final ConcurrentHashMap<String, SensorsSnapshotAvro> sensorSnapshotsCache = new ConcurrentHashMap<>();
    private final Map<TopicPartition, OffsetAndMetadata> snapshotConsumerOffsets = new HashMap<>();

    private final String className = this.getClass().getSimpleName();
    private final JsonMapper jsonMapper;
    private final SensorEventDeserializer sensorEventDeserializer;

    public AggregationStarter(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
        this.eventConsumer = new KafkaConsumer<>(CollectorConfig.getSensorEventConsumerProperties());
        this.snapshotConsumer = new KafkaConsumer<>(CollectorConfig.getSensorSnapshotConsumerProperties());
        this.snapshotProducer = new KafkaProducer<>(CollectorConfig.getProducerProperties());
        this.sensorEventDeserializer = new SensorEventDeserializer();
    }

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */
    public void start(String sensorsTopic,
                      String snapshotsTopic) {
        if (sensorsTopic == null || snapshotsTopic == null) {
            log.warn("{}: SENSORS_TOPIC and SNAPSHOTS_TOPIC must be set (environment variables).", className);
            throw new IllegalArgumentException("SENSORS_TOPIC and SNAPSHOTS_TOPIC must be set (environment variables).");
        }

        eventConsumer.subscribe(List.of(sensorsTopic));
        log.trace("{}: eventConsumer subscribed to topic {}", className, sensorsTopic);
        Runtime.getRuntime().addShutdownHook(new Thread(eventConsumer::wakeup));

        snapshotConsumer.subscribe(List.of(snapshotsTopic));
        log.trace("{}: snapshotConsumer subscribed to topic {}", className, snapshotsTopic);

        try {
            /*
             * Поток для обработки событий от датчиков.
             * Получает события, обновляет состояние датчиков и
             * отправляет обновлённые снимки состояния в соответствующий топик.
             */
            Thread eventConsumerThread = new Thread(() -> {
                while (true) {
                    try {
                        ConsumerRecords<Void, SpecificRecordBase> records = eventConsumer.poll(Duration.ofMillis(1_000));
                        log.trace("{}:sensorEventConsumer polled records: {}", className, jsonMapper.writeValueAsString(records));
                        int count = 0;

                        for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                            Optional<SensorsSnapshotAvro> snapshotAvro =
                                    updateState((SensorEventAvro) record.value());
                            manageOffsets(count, record, eventConsumer, eventConsumerOffsets);

                            if (snapshotAvro.isPresent()) {
                                ProducerRecord<Void, SpecificRecordBase> producerRecord =
                                        new ProducerRecord<>(snapshotsTopic, snapshotAvro.get());
                                snapshotProducer.send(producerRecord).get();
                                log.trace("{}: sent Avro message to topic {}: {}", className, snapshotsTopic,
                                        jsonMapper.writeValueAsString(snapshotAvro.get()));
                            }

                            count++;
                        }
                        eventConsumer.commitAsync();
                    } catch (WakeupException ignored) {

                    } catch (Exception e) {
                        log.error("{}: error acquired during processing of sensorEvents in eventConsumerThread. Exception: {}",
                                className, e, e);
                    }
                }
            });

            /* Поток для обработки снимков состояния датчиков.
             * Получает снимки и обновляет кэш полученными данными.
             */
            Thread snapshotConsumerThread = new Thread(() -> {
                while (true) {
                    try {
                        ConsumerRecords<Void, SpecificRecordBase> records = snapshotConsumer.poll(Duration.ofMillis(1_000));
                        log.trace("{}: snapshotConsumer polled records: {}", className, jsonMapper.writeValueAsString(records));
                        int count = 0;

                        for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                            SensorsSnapshotAvro snapshot = (SensorsSnapshotAvro) record.value();
                            sensorSnapshotsCache.put(snapshot.getHubId(), snapshot);
                            log.trace("{}: snapshotConsumerThread put to cache snapshot: {}",
                                    className, jsonMapper.writeValueAsString(snapshot));
                            manageOffsets(count, record, snapshotConsumer, snapshotConsumerOffsets);
                            count++;
                        }

                        snapshotConsumer.commitAsync();
                    } catch (WakeupException ignored) {

                    } catch (Exception e) {
                        log.error("{}: error acquired during processing of sensorSnapshots in snapshotConsumerThread. Exception: {}",
                                className, e, e);
                    }
                }
            });

            eventConsumerThread.start();
            snapshotConsumerThread.start();
        } catch (Exception e) {
            log.error("{}: error acquired in start()", className, e);
        }
    }

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = null;

        try {
            // Проверяем, есть ли в кэше снапшот для event.getHubId()
            if (sensorSnapshotsCache.containsKey(event.getHubId())) {
                snapshot = sensorSnapshotsCache.get(event.getHubId());
            }

            if (snapshot == null) {
                SensorStateAvro sensorStateData = SensorStateAvro.newBuilder()
                        .setTimestamp(event.getTimestamp())
                        .setData(event.getPayload())
                        .build();
                Map<String, SensorStateAvro> sensorStateMap = new HashMap<>();


                sensorStateMap.put(event.getId(), sensorStateData);

                // Если нет, то созадём новый
                snapshot = SensorsSnapshotAvro.newBuilder()
                        .setHubId(event.getHubId())
                        .setTimestamp(event.getTimestamp())
                        .setSensorsState(sensorStateMap)
                        .build();

                log.trace("{}: result of updateState() - created new snapshot for hubId {}: {}",
                        className, event.getHubId(), jsonMapper.writeValueAsString(snapshot));
                return Optional.of(snapshot);
            }

            // Проверяем, есть ли в снапшоте данные для event.getId()
            // Если данные есть, то достаём их в переменную oldState
            SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
            if (oldState != null) {
                if (oldState.getTimestamp().isAfter(event.getTimestamp())
                        || oldState.getData().equals(event.getPayload())) {
                    // Проверка, если oldState.getTimestamp() произошёл позже, чем
                    // event.getTimestamp() или oldState.getData() равен
                    // event.getPayload(), то ничего обнавлять не нужно, выходим из метода
                    // вернув Optional.empty()
                    log.trace("{}: result of updateState() - no update required for sensor id {} in hubId {}.",
                            className, event.getId(), event.getHubId());
                    return Optional.empty();
                }
            }

            // если дошли до сюда, значит, пришли новые данные и
            // снапшот нужно обновить
            // Создаём экземпляр SensorStateAvro на основе данных события
            SensorStateAvro sensorStateData = SensorStateAvro.newBuilder()
                    .setTimestamp(event.getTimestamp())
                    .setData(event.getPayload())
                    .build();

            // Добавляем полученный экземпляр в снапшот
            snapshot.getSensorsState().put(event.getId(), sensorStateData);
            // Обновляем таймстемп снапшота таймстемпом из события
            snapshot.setTimestamp(event.getTimestamp());
            // Возвращаем снапшот - Optional.of(snapshot)
            log.trace("{}: result of updateState() - updated snapshot for hubId {}: {}",
                    className, event.getHubId(), jsonMapper.writeValueAsString(snapshot));
            return Optional.of(snapshot);
        } catch (JsonProcessingException e) {
            log.warn("{}: error acquired while processing JSON in updateState(): {}",
                    className, e.getMessage(), e);
            return Optional.empty();
        }
    }

    private void manageOffsets(int count,
                               ConsumerRecord<Void, SpecificRecordBase> record,
                               KafkaConsumer<Void, SpecificRecordBase> consumer,
                               Map<TopicPartition, OffsetAndMetadata> consumerOffsets) {
        consumerOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(consumerOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("{}: error acquired while commiting offsets: {}", className, offsets, exception);
                }
            });
        }
    }

    @Override
    public void close() throws Exception {
        snapshotProducer.flush();
        snapshotProducer.close(Duration.ofSeconds(10));
        eventConsumer.commitSync(eventConsumerOffsets);
        eventConsumer.close(Duration.ofSeconds(10));
        snapshotConsumer.commitSync(snapshotConsumerOffsets);
        snapshotConsumer.close(Duration.ofSeconds(10));
    }
}