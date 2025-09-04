package ru.yandex.practicum.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.collector.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorEventAvro;

@Component
public class TemperatureSensorEventHandler extends BaseSensorEventHandler<TemperatureSensorEventAvro> {
    public TemperatureSensorEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected TemperatureSensorEventAvro mapToAvro(SensorEvent event) {
        TemperatureSensorEvent _event = (TemperatureSensorEvent) event;
        return TemperatureSensorEventAvro.newBuilder()
                .setId(_event.getId())
                .setHubId(_event.getHubId())
                .setTimestamp(_event.getTimestamp())
                .setTemperatureC(_event.getTemperatureC())
                .setTemperatureF(_event.getTemperatureF())
                .setType(SensorEventType.toAvro(_event.getType()))
                .build();
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
