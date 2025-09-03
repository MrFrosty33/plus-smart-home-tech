package ru.practicum.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceAction {
    @NotBlank
    private String sensorId;
    @NotNull
    private ActionType type;
    private int value;
}
