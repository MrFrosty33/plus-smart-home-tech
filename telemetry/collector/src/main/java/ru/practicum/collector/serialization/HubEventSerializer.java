package ru.practicum.collector.serialization;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import ru.practicum.collector.model.hub.HubEvent;

@Slf4j
public class HubEventSerializer implements Serializer<HubEvent> {
    private final JsonMapper jsonMapper = new JsonMapper();
    private final String className = this.getClass().getSimpleName();

    public HubEventSerializer() {
        // пустой конструктор нужен для Kafka, иначе не запускается
    }

    @Override
    public byte[] serialize(String topic, HubEvent data) {
        try {
            log.trace("{}: serializing HubEvent: {}", className, data);
            return jsonMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing HubEvent", e);
        }
    }
}
