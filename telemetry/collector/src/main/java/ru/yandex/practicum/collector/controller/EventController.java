package ru.yandex.practicum.collector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventController {
    private final AvroKafkaProducer eventService;
    private final String className = this.getClass().getSimpleName();

//    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
//    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

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
        log.trace("{}: received SensorEvent: {}", className, sensorEvent);

        //eventService.produceSensorData(sensorEvent);
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void receiveHubEvent(@RequestBody @Valid HubEvent hubEvent) {
        log.trace("{}: received HubEvent: {}", className, hubEvent);
        //eventService.produceHubData(hubEvent);
    }
}
