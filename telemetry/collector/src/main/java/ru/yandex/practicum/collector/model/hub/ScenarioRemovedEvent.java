package ru.yandex.practicum.collector.model.hub;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {
    @Min(3)
    @NotBlank
    private String name;

    @Override
    @NotNull
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
