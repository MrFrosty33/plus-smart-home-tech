package ru.yandex.practicum.analyzer.service.handler.hub;

import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScenarioRemovedHandler implements HubEventHandler<ScenarioRemovedEventAvro> {
    private final ScenarioRepository scenarioRepository;
    private final String className = this.getClass().getSimpleName();

    @Override
    public void handleEvent(ScenarioRemovedEventAvro data, String hubId) {
        try {
            log.trace("{}: handling ScenarioRemovedEventAvro: {}, hubId: {}", className, data, hubId);
            scenarioRepository.delete(scenarioRepository.findByHubIdAndName(hubId, data.getName()).orElseThrow());
            log.trace("{}: Scenario with hubId: {} and name: {} removed", className, hubId, data.getName());
        } catch (PersistenceException e) {
            log.error("{}: Persistence Error handling DeviceAddedEventAvro: {}", className, e.getMessage());
        } catch (DataAccessException e) {
            log.error("{}: Data Access Error handling DeviceAddedEventAvro: {}", className, e.getMessage());
        } catch (Exception e) {
            log.error("{}: Error handling DeviceAddedEventAvro: {}", className, e.getMessage());
        }
    }

    @Override
    public Class<ScenarioRemovedEventAvro> getHandledEventClass() {
        return ScenarioRemovedEventAvro.class;
    }
}
