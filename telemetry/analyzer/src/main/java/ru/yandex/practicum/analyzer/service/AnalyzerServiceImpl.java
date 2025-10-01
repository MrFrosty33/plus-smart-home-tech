package ru.yandex.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class AnalyzerServiceImpl implements AnalyzerService {
    private final Map<Class<?>, HubEventHandler<?>> handlers = new HashMap<>();

    private final String className = AnalyzerServiceImpl.class.getSimpleName();

    public AnalyzerServiceImpl(Set<HubEventHandler<?>> handlers) {
        handlers.forEach(handler -> this.handlers.put(handler.getHandledEventClass(), handler));
    }

    @Override
    public void processHubEvent(HubEventAvro event) {
        try {
            log.trace("{}: processing hub event: {}", className, event);
            Object payload = event.getPayload();
            HubEventHandler<?> handler = handlers.get(payload.getClass());
            if (handler != null) {
                ((HubEventHandler<Object>) handler).handleEvent(payload);
            }
        } catch (Exception e) {
            log.error("{}: error processing hub event: {}", className, event, e);
        }
    }

    @Override
    public void processSensorSnapshot(SensorsSnapshotAvro snapshot) {
        // на вход ещё сценарии принимает??
        //todo как-то надо это всё вместе связать
//        Когда анализатор получает очередной снапшот от конкретного хаба,
//        он загружает сценарии, связанные с этим хабом через hubId, в память для дальнейшего анализа.

//        Внутри сценария хранится список объектов-условий.
//        Каждое условие представляет собой класс. Такой класс содержит информацию о типе датчика (поле type),
//        который будет анализироваться, операцию (поле )
//        и опорное значение, с которым будет сравниваться значение датчика в процессе проверки.

//        Кроме условий, сценарий содержит список действий. Каждое действие связано с устройствами через поле type.
//        Внутри сущности «действие» также есть целочисленное поле value, которое интерпретируется устройствами по-разному.
//        Например, для кондиционера value может обозначать установленную целевую температуру.
//        А для вентилятора — состояние «вкл/выкл» (где «включено» — это 1, а «выключено» — 0).

//        Реализуйте алгоритм вычисления списка действий на выполнение.
//        На вход этот алгоритм будет принимать снапшот состояния хаба и связанные с ним сценарии.
//        Проходя по списку условий, проверяйте истинность каждого из них.

    }
}
