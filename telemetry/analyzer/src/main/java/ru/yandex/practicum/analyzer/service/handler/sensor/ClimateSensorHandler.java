package ru.yandex.practicum.analyzer.service.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.dto.ConditionDto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Slf4j
@Component
public class ClimateSensorHandler implements SensorEventHandler<ClimateSensorAvro> {
    private final String className = ClimateSensorHandler.class.getSimpleName();

    @Override
    public boolean processSensorCondition(ClimateSensorAvro data, ConditionDto condition) {
        switch (condition.getType()) {
            case TEMPERATURE -> {
                int sensorValue = data.getTemperatureC();
                return processOperation(sensorValue, condition);
            }
            case HUMIDITY -> {
                int sensorValue = data.getHumidity();
                return processOperation(sensorValue, condition);
            }
            case CO2LEVEL -> {
                int sensorValue = data.getCo2Level();
                return processOperation(sensorValue, condition);
            }
            default -> {
                log.warn("{}: unsupported condition type: {}", className, condition.getType());
                throw new UnsupportedOperationException("Unsupported condition type " + condition.getType());
            }
        }
    }

    @Override
    public boolean processOperation(int sensorValue, ConditionDto condition) {
        try {
            switch (condition.getOperation()) {
                case EQUALS -> {
                    return sensorValue == condition.getValue();
                }
                case GREATER_THAN -> {
                    return sensorValue > condition.getValue();
                }
                case LOWER_THAN -> {
                    return sensorValue < condition.getValue();
                }
                default -> {
                    log.warn("{}: unsupported operation: {}", className, condition.getOperation());
                    throw new UnsupportedOperationException("Unsupported operation " + condition.getOperation());
                }
            }
        } catch (NullPointerException e) {
            log.error("{}: NullPointerException processing operation: {}, sensorValue: {}, conditionValue: {}",
                    className, condition.getOperation(), sensorValue, condition.getValue(), e);
            throw e;
        }
    }


    @Override
    public Class<ClimateSensorAvro> getHandledSensorClass() {
        return ClimateSensorAvro.class;
    }
}
