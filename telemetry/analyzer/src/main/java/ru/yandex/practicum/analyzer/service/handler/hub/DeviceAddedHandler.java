package ru.yandex.practicum.analyzer.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;

@Component
@Slf4j
public class DeviceAddedHandler implements HubEventHandler<DeviceAddedEventAvro> {
    @Override
    public void handleEvent(DeviceAddedEventAvro event) {
        // проверяем, существует ли уже такая запись в БД
        // если нет, то создаём
        // если да, то сравниваем, если отличается - обновляем
    }

    @Override
    public Class<DeviceAddedEventAvro> getHandledEventClass() {
        return DeviceAddedEventAvro.class;
    }
}
