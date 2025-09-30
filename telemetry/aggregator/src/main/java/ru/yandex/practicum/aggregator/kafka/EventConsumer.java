package ru.yandex.practicum.aggregator.kafka;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.AggregatorConfig;
import ru.yandex.practicum.aggregator.cache.SharedSensorSnapshotsCache;
import ru.yandex.practicum.aggregator.service.OffsetsManager;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class EventConsumer implements AutoCloseable {
    private final KafkaConsumer<Void, SpecificRecordBase> eventConsumer;
    private final Map<TopicPartition, OffsetAndMetadata> eventConsumerOffsets = new HashMap<>();

    private final KafkaProducer<Void, SpecificRecordBase> snapshotProducer;
    private final SharedSensorSnapshotsCache cache;

    private final OffsetsManager offsetsManager;

    private final String sensorsTopic;
    private final String snapshotsTopic;


    private final JsonMapper jsonMapper;
    private final String className = EventConsumer.class.getSimpleName();

    private volatile boolean running = true;

    public EventConsumer(OffsetsManager offsetsManager,
                         JsonMapper jsonMapper,
                         SharedSensorSnapshotsCache cache,
                         @Value("${SENSORS_TOPIC}") String sensorsTopic,
                         @Value("${SNAPSHOTS_TOPIC}") String snapshotsTopic) {
        this.eventConsumer = new KafkaConsumer<>(AggregatorConfig.getSensorEventConsumerProperties());
        this.snapshotProducer = new KafkaProducer<>(AggregatorConfig.getProducerProperties());
        this.offsetsManager = offsetsManager;
        this.jsonMapper = jsonMapper;
        this.cache = cache;
        this.sensorsTopic = sensorsTopic;
        this.snapshotsTopic = snapshotsTopic;
    }

    public void start() {
        eventConsumer.subscribe(Collections.singletonList(sensorsTopic));
        log.trace("{}: subscribed to topic {}", className, sensorsTopic);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.trace("{}: shutdown hook triggered", className);
            running = false;
            eventConsumer.wakeup();
        }));

        try {
            while (running) {

                ConsumerRecords<Void, SpecificRecordBase> records = eventConsumer.poll(Duration.ofMillis(1_000));
                log.trace("{}:sensorEventConsumer polled records: {}", className, jsonMapper.writeValueAsString(records));
                int count = 0;

                for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                    Optional<SensorsSnapshotAvro> snapshotAvro =
                            updateState((SensorEventAvro) record.value());
                    offsetsManager.manageOffsets(count, record, eventConsumer, eventConsumerOffsets);

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
            }
        } catch (WakeupException e) {
            if (running) {
                log.warn("{}: unexpected wakeup exception", className, e);
            } else {
                log.info("{}: woken up for shutdown", className);
            }
        } catch (Exception e) {
            log.error("{}: error acquired during processing of sensorEvents in eventConsumerThread. Exception: {}",
                    className, e, e);
        } finally {
            log.info("{}: closing eventConsumer and snapshotProducer", className);
            snapshotProducer.flush();
            snapshotProducer.close(Duration.ofSeconds(10));
            eventConsumer.commitSync(eventConsumerOffsets);
            eventConsumer.close(Duration.ofSeconds(10));
            log.info("{}: finished", className);
        }
    }

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = null;

        try {
            // Проверяем, есть ли в кэше снапшот для event.getHubId()
            if (cache.contains(event.getHubId())) {
                snapshot = cache.get(event.getHubId());
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
            log.error("{}: error acquired while processing JSON in updateState(): {}",
                    className, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public void close() {
        try {
            eventConsumer.wakeup();
        } catch (Exception e) {
            log.error("{}: error acquired during closing of eventConsumer and snapshotProducer. Exception: {}",
                    className, e, e);
        }
    }
}
