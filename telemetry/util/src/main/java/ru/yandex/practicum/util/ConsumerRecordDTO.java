package ru.yandex.practicum.util;

import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Data
public class ConsumerRecordDTO {
    private String topic;
    private int partition;
    private long offset;
    private long timestamp;
    private Object key;
    private Object value;

    public ConsumerRecordDTO(ConsumerRecord<?, ?> record) {
        this.topic = record.topic();
        this.partition = record.partition();
        this.offset = record.offset();
        this.timestamp = record.timestamp();
        this.key = record.key();
        this.value = record.value();
    }
}
