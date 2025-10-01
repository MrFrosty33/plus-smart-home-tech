package ru.yandex.practicum.analyzer.service.handler.hub;

public interface HubEventHandler<T> {
    void handleEvent(T event);

    Class<T> getHandledEventClass();
}
