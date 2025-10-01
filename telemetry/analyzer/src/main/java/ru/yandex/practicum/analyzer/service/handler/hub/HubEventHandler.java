package ru.yandex.practicum.analyzer.service.handler.hub;

public interface HubEventHandler<T> {
    void handleEvent(T data, String hubId);

    Class<T> getHandledEventClass();
}
