package ru.practicum.collector.model.hub;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    @NotBlank
    @Min(3)
    private String name;
    @NotEmpty
    private List<ScenarioCondition> conditions;
    @NotEmpty
    private List<DeviceAction> actions;

    @Override
    @NotNull
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
