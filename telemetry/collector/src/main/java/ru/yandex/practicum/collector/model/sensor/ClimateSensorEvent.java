package ru.yandex.practicum.collector.model.sensor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {
    @Min(-274)
    private int temperatureC;
    @Positive
    private int humidity;
    @Positive
    private int co2Level;

    @Override
    @NotNull
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
