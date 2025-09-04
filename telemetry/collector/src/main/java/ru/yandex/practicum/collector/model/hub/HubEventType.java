package ru.yandex.practicum.collector.model.hub;

import ru.yandex.practicum.kafka.telemetry.event.HubEventTypeAvro;

public enum HubEventType {
    DEVICE_ADDED,
    DEVICE_REMOVED,
    SCENARIO_ADDED,
    SCENARIO_REMOVED;

    public static HubEventTypeAvro toAvro(HubEventType eventType) {
        return HubEventTypeAvro.valueOf(eventType.name());
    }
}
