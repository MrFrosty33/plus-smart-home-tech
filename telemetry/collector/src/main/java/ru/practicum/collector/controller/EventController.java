package ru.practicum.collector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.service.EventService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventController {
    private final EventService eventService;
    private final String className = this.getClass().getSimpleName();

    @RequestMapping("/sensors")
    public void receiveSensorEvent(@RequestBody @Valid SensorEvent sensorEvent) {
        log.trace("{}: received SensorEvent: {}", className, sensorEvent);
        eventService.produceSensorData(sensorEvent);
    }

    @RequestMapping("/hubs")
    public void receiveHubEvent(@RequestBody @Valid HubEvent hubEvent) {
        log.trace("{}: received HubEvent: {}", className, hubEvent);
        eventService.produceHubData(hubEvent);
    }
}
