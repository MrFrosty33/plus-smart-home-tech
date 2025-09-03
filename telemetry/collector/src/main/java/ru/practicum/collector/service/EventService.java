package ru.practicum.collector.service;

import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.sensor.SensorEvent;

public interface EventService {
    void produceSensorData(SensorEvent sensorEvent);

    void produceHubData(HubEvent hubEvent);
}
