package ru.yandex.practicum.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;

@Slf4j
public class OffsetsManager {
    private final String className = OffsetsManager.class.getSimpleName();

    public void manageOffsets(int count,
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
}
