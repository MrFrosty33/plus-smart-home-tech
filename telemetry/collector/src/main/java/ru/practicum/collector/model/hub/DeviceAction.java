package ru.practicum.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceAction {
    @NotBlank
    private String sensorId;
    private ActionType type;
    private int value;
}
