package ru.practicum.collector.serialization;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import ru.practicum.collector.model.sensor.SensorEvent;

@Slf4j
public class SensorEventSerializer implements Serializer<SensorEvent> {
    private final JsonMapper jsonMapper = new JsonMapper();
    private final String className = this.getClass().getSimpleName();

    public SensorEventSerializer() {
        jsonMapper.registerModule(new JavaTimeModule());
        // конструктор нужен для Kafka, иначе не запускается
    }

    @Override
    public byte[] serialize(String topic, SensorEvent data) {
        try {
            log.trace("{}: serializing SensorEvent: {}", className, data);
            return jsonMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            log.trace("{}: caught error serializing SensorEvent: {}", className, e.getMessage());
            throw new SerializationException("Error serializing SensorEvent", e);
        }
    }
}
