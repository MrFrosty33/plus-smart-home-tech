package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.repository.ActionRepository;
import ru.yandex.practicum.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@RequiredArgsConstructor
@Service
@Slf4j
public class AnalyzerServiceImpl implements AnalyzerService {
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;

    private final String className = AnalyzerServiceImpl.class.getSimpleName();

    @Override
    public void processHubEvent(HubEventAvro event) {
        log.trace("{}: processing hub event: {}", className, event);
        // пу-пу-пу
    }

    @Override
    public void processSensorSnapshot(SensorsSnapshotAvro snapshot) {

    }
}
