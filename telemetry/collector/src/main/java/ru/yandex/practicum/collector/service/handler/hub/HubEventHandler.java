package ru.yandex.practicum.collector.service.handler.hub;

import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.collector.model.hub.HubEventType;

public interface HubEventHandler {
    HubEventType getType();

    void handle(String topic, HubEvent event);
}
