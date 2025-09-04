package ru.yandex.practicum.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorEventAvro;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorEventAvro> {
    public MotionSensorEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected MotionSensorEventAvro mapToAvro(SensorEvent event) {
        MotionSensorEvent _event = (MotionSensorEvent) event;
        return MotionSensorEventAvro.newBuilder()
                .setId(_event.getId())
                .setHubId(_event.getHubId())
                .setTimestamp(_event.getTimestamp())
                .setLinkQuality(_event.getLinkQuality())
                .setMotion(_event.isMotion())
                .setVoltage(_event.getVoltage())
                .setType(SensorEventType.toAvro(_event.getType()))
                .build();
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

}
