package ru.yandex.practicum.collector.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.collector.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.collector.service.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.config.telemetry.TopicConfig;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
@Slf4j
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final JsonMapper jsonMapper;
    private final String className = this.getClass().getSimpleName();

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    private final String sensorTopic;
    private final String hubTopic;

    public EventController(Set<SensorEventHandler> sensorEventHandlers,
                           Set<HubEventHandler> hubEventHandlers,
                           JsonMapper jsonMapper,
                           TopicConfig topics) {
        this.jsonMapper = jsonMapper;
        this.sensorTopic = topics.getSensors();
        this.hubTopic = topics.getHubs();
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.trace("{}: received SensorEvent: {}", className, jsonMapper.writeValueAsString(request));
            if (sensorEventHandlers.containsKey(request.getPayloadCase())) {
                sensorEventHandlers.get(request.getPayloadCase()).handle(sensorTopic, request);
            } else {
                log.warn("{}: No handler found for sensor event payloadCase: {}", className, request.getPayloadCase());
                throw new IllegalArgumentException("No handler found for sensor event payloadCase: " + request.getPayloadCase());
            }

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.trace("{}: received HubEvent: {}", className, jsonMapper.writeValueAsString(request));
            if (hubEventHandlers.containsKey(request.getPayloadCase())) {
                hubEventHandlers.get(request.getPayloadCase()).handle(hubTopic, request);
            } else {
                log.warn("{}: No handler found for hub event payloadCase: {}", className, request.getPayloadCase());
                throw new IllegalArgumentException("No handler found for hub event payloadCase: " + request.getPayloadCase());
            }

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
