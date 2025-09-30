package ru.yandex.practicum.aggregator.cache;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SharedSensorSnapshotsCache {
    private final Map<String, SensorsSnapshotAvro> cache = new ConcurrentHashMap<>();

    public SensorsSnapshotAvro get(String key) {
        return cache.get(key);
    }

    public void put(String key, SensorsSnapshotAvro value) {
        cache.put(key, value);
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }
}
