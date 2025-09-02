package ru.practicum.collector.model.sensor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {
    @Min(-274)
    private int temperatureC;
    @Positive
    private int humidity;
    @Positive
    private int co2level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
