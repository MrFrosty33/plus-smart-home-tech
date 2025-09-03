package ru.practicum.collector.model.sensor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class LightSensorEvent extends SensorEvent {
    private int linkQuality;
    @Positive
    private int luminosity;

    @Override
    @NotNull
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
