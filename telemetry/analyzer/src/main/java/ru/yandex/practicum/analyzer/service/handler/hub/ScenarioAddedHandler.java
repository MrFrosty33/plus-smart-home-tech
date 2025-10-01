package ru.yandex.practicum.analyzer.service.handler.hub;

import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.repository.ActionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler<ScenarioAddedEventAvro> {
    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final String className = this.getClass().getSimpleName();

    @Override
    public void handleEvent(ScenarioAddedEventAvro data, String hubId) {
        try {
            log.trace("{}: handling ScenarioAddedEventAvro: {}, hubId: {}", className, data, hubId);
            Optional<Scenario> optionalScenario = scenarioRepository.findByHubIdAndName(hubId, data.getName());
            if (optionalScenario.isPresent()) {
                Scenario scenario = optionalScenario.get();
                // проверять и обновлять hubId & name не нужно - они в условии поиска
                //todo затык здесь
                // DeviceActionAvro не сходится с Action (нет id у одного, нет sensorId у другого)
                if (!data.getActions().equals(scenario.getActions())) {
                    data.getActions().stream()
                            .filter(actionAvro -> {
                                return !actionAvro.equals(scenario.getActions().values());
                            })
                            .forEach(actionAvro -> {
                                //scenario.getActions().replace(actionAvro.getSensorId(), actionRepository.findById(ac));
                            });

                    log.trace("{}: Scenario updated: {}", className, scenario);
                }
                log.trace("{}: Scenario already exists: {}", className, scenario);
            } else {
                Scenario scenario = new Scenario();
                //todo
                scenarioRepository.save(scenario);
                log.trace("{}: Scenario created: {}", className, scenario);
            }

        } catch (PersistenceException e) {
            log.error("{}: Persistence Error handling DeviceAddedEventAvro: {}", className, e.getMessage());
        } catch (DataAccessException e) {
            log.error("{}: Data Access Error handling DeviceAddedEventAvro: {}", className, e.getMessage());
        } catch (Exception e) {
            log.error("{}: Error handling DeviceAddedEventAvro: {}", className, e.getMessage());
        }
    }

    @Override
    public Class<ScenarioAddedEventAvro> getHandledEventClass() {
        return ScenarioAddedEventAvro.class;
    }
}
