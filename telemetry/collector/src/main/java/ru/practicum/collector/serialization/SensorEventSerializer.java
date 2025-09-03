package ru.practicum.collector.serialization;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import ru.practicum.collector.model.sensor.SensorEvent;

@RequiredArgsConstructor
@Slf4j
public class SensorEventSerializer implements Serializer<SensorEvent> {
    private final JsonMapper jsonMapper;
    private final String className = this.getClass().getSimpleName();

    @Override
    public byte[] serialize(String topic, SensorEvent data) {
        try {
            log.trace("{}: serializing SensorEvent: {}", className, data);
            return jsonMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing SensorEvent", e);
        }
    }
}
