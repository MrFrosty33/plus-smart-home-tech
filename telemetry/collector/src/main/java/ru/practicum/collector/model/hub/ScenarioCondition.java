package ru.practicum.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class ScenarioCondition {
    @NotBlank
    private String sensorId;
    private ConditionType type;
    private OperationType operation;
    private int value;
}
