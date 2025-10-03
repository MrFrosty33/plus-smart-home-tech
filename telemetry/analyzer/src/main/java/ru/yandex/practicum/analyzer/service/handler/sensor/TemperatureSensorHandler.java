package ru.yandex.practicum.analyzer.service.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.dto.ConditionDto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
@Slf4j
public class TemperatureSensorHandler implements SensorEventHandler<TemperatureSensorAvro> {
    private final String className = this.getClass().getSimpleName();

    @Override
    public boolean processSensorCondition(TemperatureSensorAvro data, ConditionDto condition) {
        switch (condition.getType()) {
            case TEMPERATURE -> {
                //todo прояснить
                // тут вопрос, какое значение брать? Цельсий или Фаренгейт
                // есть тип кондиции только на температуру, без уточнения...
                // и также неизвестно, какая из систем исчисления используется в значениях условия :/
                // возьму пока всё в Цельсиях
                int sensorValue = data.getTemperatureC();
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
    public Class<TemperatureSensorAvro> getHandledSensorClass() {
        return TemperatureSensorAvro.class;
    }
}