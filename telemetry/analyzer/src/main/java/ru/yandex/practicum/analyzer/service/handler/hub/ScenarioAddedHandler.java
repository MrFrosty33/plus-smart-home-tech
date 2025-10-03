package ru.yandex.practicum.analyzer.service.handler.hub;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.*;
import ru.yandex.practicum.analyzer.repository.ActionRepository;
import ru.yandex.practicum.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler<ScenarioAddedEventAvro> {
    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final String className = this.getClass().getSimpleName();

    @Override
    @Transactional
    public void handleEvent(ScenarioAddedEventAvro data, String hubId) {
        try {
            log.trace("{}: handling ScenarioAddedEventAvro: {}, hubId: {}", className, data, hubId);
            Optional<Scenario> optionalScenario = scenarioRepository.findByHubIdAndName(hubId, data.getName());
            if (optionalScenario.isPresent()) {
                Scenario scenario = optionalScenario.get();
                // проверять и обновлять hubId & name не нужно - они в условии поиска

                data.getActions()
                        .forEach(actionAvro -> {
                            boolean updated = false;
                            Action actionEntity = scenario.getActions().get(actionAvro.getSensorId());

                            if (actionEntity == null) {
                                actionEntity = createNewAction(actionAvro);
                                scenario.getActions().put(actionAvro.getSensorId(), actionEntity);
                                log.trace("{}: Action created and added to Scenario actions: {}", className, actionEntity);
                                return;
                            }

                            if (!actionEntity.getType().equals(
                                    ActionType.fromString(actionAvro.getType().toString()))) {
                                actionEntity.setType(ActionType.fromString(actionAvro.getType().toString()));
                                updated = true;
                                log.trace("{}: Action type updated: {}", className, actionEntity);
                            }
                            if (!actionEntity.getValue().equals(actionAvro.getValue())) {
                                actionEntity.setValue(actionAvro.getValue());
                                updated = true;
                                log.trace("{}: Action value updated: {}", className, actionEntity);
                            }
                            if (updated) {
                                actionRepository.save(actionEntity);
                                scenario.getActions().replace(actionAvro.getSensorId(), actionEntity);
                                log.trace("{}: Action saved and replaced in Scenario actions", className);
                            }
                        });

                data.getConditions()
                        .forEach(conditionAvro -> {
                            boolean updated = false;
                            Condition conditionEntity = scenario.getConditions().get(conditionAvro.getSensorId());

                            if (conditionEntity == null) {
                                conditionEntity = createNewCondition(conditionAvro);
                                scenario.getConditions().put(conditionAvro.getSensorId(), conditionEntity);
                                log.trace("{}: Condition created and added to Scenario conditions: {}", className, conditionEntity);
                                return;
                            }

                            if (!conditionEntity.getType().equals(
                                    ConditionType.fromString(conditionAvro.getType().toString()))) {
                                conditionEntity.setType(
                                        ConditionType.fromString(conditionAvro.getType().toString()));
                                updated = true;
                                log.trace("{}: Condition type updated: {}", className, conditionEntity);
                            }
                            if (!conditionEntity.getOperation().equals(
                                    ConditionOperation.fromString(conditionAvro.getOperation().toString()))) {
                                conditionEntity.setOperation(
                                        ConditionOperation.fromString(conditionAvro.getOperation().toString()));
                                updated = true;
                                log.trace("{}: Condition operation updated: {}", className, conditionEntity);
                            }
                            Integer newValue = null;
                            if (conditionAvro.getValue() instanceof Boolean) {
                                newValue = (Boolean) conditionAvro.getValue() ? 1 : 0;
                            } else {
                                newValue = (Integer) conditionAvro.getValue();
                            }

                            if (!conditionEntity.getValue().equals(newValue)) {
                                conditionEntity.setValue(newValue);
                                updated = true;
                                log.trace("{}: Condition value updated: {}", className, conditionEntity);
                            }
                            if (updated) {
                                conditionRepository.save(conditionEntity);
                                scenario.getConditions().replace(conditionAvro.getSensorId(), conditionEntity);
                                log.trace("{}: Condition saved and replaced in Scenario conditions", className);
                            }
                        });
            } else {
                Scenario scenario = new Scenario();
                scenario.setHubId(hubId);
                scenario.setName(data.getName());

                data.getActions()
                        .forEach(actionAvro -> {
                            Action actionEntity = createNewAction(actionAvro);
                            scenario.getActions().put(actionAvro.getSensorId(), actionEntity);
                            log.trace("{}: Action created and added to new Scenario actions: {}", className, actionEntity);
                        });
                data.getConditions()
                        .forEach(conditionAvro -> {
                            Condition conditionEntity = createNewCondition(conditionAvro);
                            scenario.getConditions().put(conditionAvro.getSensorId(), conditionEntity);
                            log.trace("{}: Condition created and added to new Scenario conditions: {}", className, conditionEntity);
                        });

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


    // теоритически, эти методы можно вынести в отдельные классы
    // но они используются только при добавления сценария и пускай пока будут здесь
    private Action createNewAction(DeviceActionAvro actionAvro) {
        Action actionEntity = new Action();
        actionEntity.setType(
                ActionType.fromString(actionAvro.getType().toString()));
        actionEntity.setValue(actionAvro.getValue());
        actionRepository.save(actionEntity);

        log.trace("{}: New Action created: {}", className, actionEntity);
        return actionEntity;
    }

    // ^^
    private Condition createNewCondition(ScenarioConditionAvro conditionAvro) {
        Condition conditionEntity = new Condition();
        conditionEntity.setType(
                ConditionType.fromString(conditionAvro.getType().toString()));
        conditionEntity.setOperation(
                ConditionOperation.fromString(conditionAvro.getOperation().toString()));
        if (conditionAvro.getValue() instanceof Boolean) {
            conditionEntity.setValue((Boolean) conditionAvro.getValue() ? 1 : 0);
        } else {
            conditionEntity.setValue((Integer) conditionAvro.getValue());
        }

        conditionRepository.save(conditionEntity);

        log.trace("{}: New Condition created: {}", className, conditionEntity);
        return conditionEntity;
    }

    @Override
    public Class<ScenarioAddedEventAvro> getHandledEventClass() {
        return ScenarioAddedEventAvro.class;
    }
}
