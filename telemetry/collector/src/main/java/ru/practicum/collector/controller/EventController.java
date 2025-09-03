package ru.practicum.collector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void receiveSensorEvent(@RequestBody @Valid SensorEvent sensorEvent) {
        log.trace("{}: received SensorEvent: {}", className, sensorEvent);
        eventService.produceSensorData(sensorEvent);
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void receiveHubEvent(@RequestBody @Valid HubEvent hubEvent) {
        log.trace("{}: received HubEvent: {}", className, hubEvent);
        eventService.produceHubData(hubEvent);
    }
}
