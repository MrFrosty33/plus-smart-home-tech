package ru.yandex.practicum.collector.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.collector.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.collector.service.handler.sensor.SensorEventHandler;

import java.util.Map;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventController {
    private final AvroKafkaProducer eventService;
    private final String className = this.getClass().getSimpleName();

    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    @Value("${SENSORS_TOPIC}")
    private String sensorTopic;
    @Value("${HUBS_TOPIC}")
    private String hubTopic;

    private final JsonMapper jsonMapper;

    //todo логировать входящие и исходящие данные в JSON???


//    @PostMapping("/sensors")
//    @ResponseStatus(HttpStatus.OK)
//    public void receiveSensorEvent(@RequestBody @Valid SensorEvent sensorEvent) {
//        log.trace("{}: received SensorEvent: {}", className, sensorEvent);
//        eventService.produceSensorData(sensorEvent);
//    }

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
