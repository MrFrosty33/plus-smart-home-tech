package ru.practicum.collector.model.sensor;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class TemperatureSensorEvent extends SensorEvent {
    @Min(-274)
    private int temperatureC;
    @Min(-460)
    private int temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
