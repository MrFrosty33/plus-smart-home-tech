package ru.practicum.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScenarioCondition {
    @NotBlank
    private String sensorId;
    @NotNull
    private ConditionType type;
    @NotNull
    private OperationType operation;
    private int value;
}
