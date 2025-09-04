package ru.yandex.practicum.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensor.LightSensorEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorEventAvro;

@Component
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorEventAvro> {
    public LightSensorEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected LightSensorEventAvro mapToAvro(SensorEvent event) {
        LightSensorEvent _event = (LightSensorEvent) event;
        return LightSensorEventAvro.newBuilder()
                .setId(_event.getId())
                .setHubId(_event.getHubId())
                .setTimestamp(_event.getTimestamp())
                .setLinkQuality(_event.getLinkQuality())
                .setLuminosity(_event.getLuminosity())
                .setType(SensorEventType.toAvro(_event.getType()))
                .build();

    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
