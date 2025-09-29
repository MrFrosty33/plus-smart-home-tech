package ru.yandex.practicum.collector.service;

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
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class AggregationStarter {
    private final KafkaConsumer<Void, SpecificRecordBase> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final KafkaProducer<Void, SpecificRecordBase> producer;

    private final String className = this.getClass().getSimpleName();
    private final JsonMapper jsonMapper;
    private final SensorEventDeserializer sensorEventDeserializer;

    public AggregationStarter(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
        this.consumer = new KafkaConsumer<>(CollectorConfig.getConsumerProperties());
        this.producer = new KafkaProducer<>(CollectorConfig.getProducerProperties());
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
        try {
            consumer.subscribe(List.of(sensorsTopic, snapshotsTopic));
            log.trace("{}: consumer subscribed to topic {}", className, sensorsTopic);
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<Void, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));
                log.trace("{}: consumer polled records: {}", className, jsonMapper.writeValueAsString(records));
                int count = 0;
                for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                    if (record.topic().equals(snapshotsTopic)) {
                        //todo возможно возникнет проблема с фиксацией смещения
                        continue;
                    }
                    Optional<SensorSnapshotAvro> snapshotAvro = updateState(records,
                            (SensorEventAvro) record.value(),
                            snapshotsTopic);
                    manageOffsets(count, record, consumer);

                    if (snapshotAvro.isPresent()) {
                        ProducerRecord<Void, SpecificRecordBase> producerRecord =
                                new ProducerRecord<>(snapshotsTopic, snapshotAvro.get());
                        producer.send(producerRecord);
                        log.trace("{}: sent Avro message to topic {}: {}", className, snapshotsTopic,
                                jsonMapper.writeValueAsString(snapshotAvro.get()));
                    }

                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("{}: error acquired during processing of events from sensors", className, e);
        } finally {
            producer.flush();
            producer.close(Duration.ofSeconds(10));
            consumer.commitSync(currentOffsets);
            consumer.close(Duration.ofSeconds(10));
        }
    }

    private void manageOffsets(int count,
                               ConsumerRecord<Void, SpecificRecordBase> record,
                               KafkaConsumer<Void, SpecificRecordBase> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("{}: error acquired while commiting offsets: {}", className, offsets, exception);
                }
            });
        }
    }

    private Optional<SensorSnapshotAvro> updateState(ConsumerRecords<Void, SpecificRecordBase> records,
                                                     SensorEventAvro event,
                                                     String snapshotsTopic) {
        SensorSnapshotAvro snapshot = null;
        int count = 0;
        for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
            if (!record.topic().equals(snapshotsTopic)) {
                manageOffsets(count, record, consumer);
                count++;
                continue;
            }

            // Проверяем, есть ли снапшот для event.getHubId()
            snapshot = (SensorSnapshotAvro) record.value();
            // Фиксируем смещение
            manageOffsets(count, record, consumer);
            count++;
            if (snapshot.getHubId().equals(event.getHubId())) {
                // Если снапшот есть, то достаём его
                break;
            }
        }

        if (snapshot == null) {
            SensorStateAvro sensorStateData = SensorStateAvro.newBuilder()
                    .setTimestamp(event.getTimestamp())
                    .setData(event.getPayload())
                    .build();
            Map<String, SensorStateAvro> sensorStateMap = new HashMap<>();

            sensorStateMap.put(event.getId(), sensorStateData);

            // Если нет, то созадём новый
            snapshot = SensorSnapshotAvro.newBuilder()
                    .setHubId(event.getHubId())
                    .setTimestamp(event.getTimestamp())
                    .setSensorsState(sensorStateMap)
                    .build();

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
        return Optional.of(snapshot);
    }
}