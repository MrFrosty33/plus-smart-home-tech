package ru.yandex.practicum.collector.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.collector.exception.JsonException;
import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.collector.model.hub.HubEventType;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.collector.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.collector.service.handler.sensor.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Validated
@RestController
@RequestMapping("/events")
public class EventController {
    private final JsonMapper jsonMapper;
    private final String className = this.getClass().getSimpleName();

    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    private final String sensorTopic;
    private final String hubTopic;

    public EventController(Set<SensorEventHandler> sensorEventHandlers,
                           Set<HubEventHandler> hubEventHandlers,
                           JsonMapper jsonMapper,
                           @Value("${SENSORS_TOPIC}") String sensorTopic,
                           @Value("${HUBS_TOPIC}") String hubTopic) {
        this.jsonMapper = jsonMapper;
        this.sensorTopic = sensorTopic;
        this.hubTopic = hubTopic;
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getType, Function.identity()));
    }

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void receiveSensorEvent(@RequestBody @Valid SensorEvent sensorEvent) {
        try {
            log.trace("{}: received SensorEvent: {}", className, jsonMapper.writeValueAsString(sensorEvent));
            if (sensorEventHandlers.containsKey(sensorEvent.getType())) {
                sensorEventHandlers.get(sensorEvent.getType()).handle(sensorTopic, sensorEvent);
            } else {
                log.warn("{}: No handler found for sensor event type: {}", className, sensorEvent.getType());
                throw new IllegalArgumentException("No handler found for sensor event type: " + sensorEvent.getType());
            }
        } catch (JsonProcessingException e) {
            log.warn("{}: Error processing SensorEvent JSON: {}", className, e.getMessage());
            throw new JsonException("Error processing SensorEvent JSON");
        }
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void receiveHubEvent(@RequestBody @Valid HubEvent hubEvent) {
        try {
            log.trace("{}: received HubEvent: {}", className, jsonMapper.writeValueAsString(hubEvent));
            if (hubEventHandlers.containsKey(hubEvent.getType())) {
                hubEventHandlers.get(hubEvent.getType()).handle(hubTopic, hubEvent);
            } else {
                log.warn("{}: No handler found for hub event type: {}", className, hubEvent.getType());
                throw new IllegalArgumentException("No handler found for hub event type: " + hubEvent.getType());
            }
        } catch (JsonProcessingException e) {
            log.warn("{}: Error processing HubEvent JSON: {}", className, e.getMessage());
            throw new JsonException("Error processing HubEvent JSON");
        }
    }
}
