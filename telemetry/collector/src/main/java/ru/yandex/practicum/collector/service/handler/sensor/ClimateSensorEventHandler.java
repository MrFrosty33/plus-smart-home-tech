package ru.yandex.practicum.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorEventAvro;

@Component
public class ClimateSensorEventHandler extends BaseSensorEventHandler<ClimateSensorEventAvro> {

    public ClimateSensorEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected ClimateSensorEventAvro mapToAvro(SensorEvent event) {
        ClimateSensorEvent _event = (ClimateSensorEvent) event;
        return ClimateSensorEventAvro.newBuilder()
                .setTemperatureC(_event.getTemperatureC())
                .setHumidity(_event.getHumidity())
                .setCo2level(_event.getCo2level())
                .build();
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
