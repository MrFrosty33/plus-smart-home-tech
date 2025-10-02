package ru.yandex.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.mapper.ConditionMapper;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.analyzer.service.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.analyzer.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.*;

@Service
@Slf4j
public class AnalyzerServiceImpl implements AnalyzerService {
    private final Map<Class<?>, HubEventHandler<?>> hubEventHandlers = new HashMap<>();
    private final Map<Class<?>, SensorEventHandler<?>> sensorEventHandlers = new HashMap<>();
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;
    private final ScenarioRepository scenarioRepository;

    private final String className = AnalyzerServiceImpl.class.getSimpleName();

    public AnalyzerServiceImpl(Set<HubEventHandler<?>> hubEventHandlers,
                               Set<SensorEventHandler<?>> sensorEventHandlers,
                               @GrpcClient("hub-router")
                               HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient,
                               ScenarioRepository scenarioRepository) {
        hubEventHandlers.forEach(handler -> this.hubEventHandlers.put(handler.getHandledEventClass(), handler));
        sensorEventHandlers.forEach(handler -> this.sensorEventHandlers.put(handler.getHandledSensorClass(), handler));
        this.hubRouterClient = hubRouterClient;
        this.scenarioRepository = scenarioRepository;
    }

    @Override
    public void processHubEvent(HubEventAvro event) {
        try {
            log.trace("{}: processing hub event: {}", className, event);
            Object payload = event.getPayload();
            HubEventHandler<?> handler = hubEventHandlers.get(payload.getClass());
            if (handler != null) {
                ((HubEventHandler<Object>) handler).handleEvent(payload, event.getHubId());
            } else {
                log.warn("{}: no handler found for hub event: {}", className, event);
            }
        } catch (Exception e) {
            log.error("{}: error processing hub event: {}", className, event, e);
        }
    }

    @Override
    public void processSensorSnapshot(SensorsSnapshotAvro snapshot) {
        log.trace("{}: processing sensor snapshot: {}", className, snapshot);
        List<Scenario> scenarios = scenarioRepository.findByHubId(snapshot.getHubId());
        log.trace("{}: found scenarios for hubId {}: {}", className, snapshot.getHubId(), scenarios);
        List<Scenario> passedScenarios = new ArrayList<>();
        for (Scenario scenario : scenarios) {
            scenario.getConditions().entrySet().forEach(entry -> {
                SensorStateAvro sensorStateAvro = snapshot.getSensorsState().get(entry.getKey());
                SensorEventHandler<?> handler = sensorEventHandlers.get(sensorStateAvro.getData().getClass());

                if (handler != null) {
                    log.trace("{}: processing sensor data: {} with condition: {}",
                            className, sensorStateAvro.getData(), entry.getValue());
                    boolean result = ((SensorEventHandler<Object>) handler)
                            .processSensorCondition(sensorStateAvro.getData(), ConditionMapper.toDto(entry.getValue()));
                    if (result) {
                        log.trace("{}: condition passed for sensorId {}: {}",
                                className, entry.getKey(), entry.getValue());
                        passedScenarios.add(scenario);
                    }
                } else {
                    log.warn("{}: no handler found for sensor data: {}", className, sensorStateAvro.getData());
                }

                for (Scenario passedScenario : passedScenarios) {
                    passedScenario.getActions().forEach((key, value) -> {
                        //todo внимательно посмотреть, что назначается. разобраться, откуда брать value, из Action?
                        DeviceActionProto actionProto = DeviceActionProto.newBuilder()
                                .setSensorId(key)
                                .setType(ActionTypeProto.valueOf(value.getType().toString()))
                                .setValue(value.getValue())
                                .build();
                        log.trace("{}: built action proto: {}", className, actionProto);
                        DeviceActionRequestProto request = DeviceActionRequestProto.newBuilder()
                                .setHubId(snapshot.getHubId())
                                .setAction(actionProto)
                                .build();
                        log.trace("{}: built action request proto: {}", className, request);
                        hubRouterClient.handleDeviceAction(request);
                        log.info("{}: sent action request to hub-router: {}", className, request);
                    });
                }

            });
        }
    }

//                это я какую-то хрень придумал :[[[[
//                Map<String, ConditionDto> conditionDtos = scenarios.stream()
//                        // сценарии, у которых ключи условий сходятся с полученными id сенсоров
//                        .filter(scenario -> sensorIds.containsAll(scenario.getConditions().keySet()))
//
//                        // сценарии, у которых типы условий совпадают с датчиком
//                        .filter(scenario ->
//                                scenario.getConditions().values().stream()
//                                        .map(Condition::getType)
//                                        .allMatch(type -> handler.getHandledSensorConditions().contains(type))
//                        )
//
//                        // разворачиваем Map<sensorId, Condition> в Stream<Map.Entry<sensorId, Condition>>
//                        .flatMap(scenario -> scenario.getConditions().entrySet().stream())
//
//                        // Собираем Map<sensorId, ConditionDto>
//                        .collect(Collectors.toMap(
//                                Map.Entry::getKey,
//                                entry -> ConditionMapper.toDto(entry.getValue())
//                        ));
//
//                Map<String, ActionDto> actionDtos = scenarios.stream()
//                        // сценарии, у которых ключи условий сходятся с полученными id сенсоров
//                        .filter(scenario -> sensorIds.containsAll(scenario.getActions().keySet()))
//                        // разворачиваем Map<sensorId, Action> в Stream<Map.Entry<sensorId, Action>>
//                        .flatMap(scenario -> scenario.getActions().entrySet().stream())
//                        // Собираем Map<sensorId, ActionDto>
//                        .collect(Collectors.toMap(
//                                Map.Entry::getKey,
//                                entry -> ActionMapper.toDto(entry.getValue())
//                        ));


    //       Когда анализатор получает очередной снапшот от конкретного хаба,
//       он загружает сценарии, связанные с этим хабом через hubId, в память для дальнейшего анализа.
//       Внутри сценария хранится список объектов-условий.
//       Каждое условие представляет собой класс. Такой класс содержит информацию о типе датчика (поле type),
//       который будет анализироваться, операцию (поле )
//       и опорное значение, с которым будет сравниваться значение датчика в процессе проверки.

//       Кроме условий, сценарий содержит список действий. Каждое действие связано с устройствами через поле type.
//       Внутри сущности «действие» также есть целочисленное поле value, которое интерпретируется устройствами по-разному.
//       Например, для кондиционера value может обозначать установленную целевую температуру.
//       А для вентилятора — состояние «вкл/выкл» (где «включено» — это 1, а «выключено» — 0).

//       Реализуйте алгоритм вычисления списка действий на выполнение.
//       На вход этот алгоритм будет принимать снапшот состояния хаба и связанные с ним сценарии.
//       Проходя по списку условий, проверяйте истинность каждого из них.


}
