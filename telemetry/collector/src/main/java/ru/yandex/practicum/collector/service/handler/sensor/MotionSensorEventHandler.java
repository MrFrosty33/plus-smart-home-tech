package ru.yandex.practicum.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorEventAvro;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorEventAvro> {
    public MotionSensorEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected MotionSensorEventAvro mapToAvro(SensorEventProto event) {
        MotionSensorProto _event = event.getMotionSensorEvent();
        return MotionSensorEventAvro.newBuilder()
                .setLinkQuality(_event.getLinkQuality())
                .setMotion(_event.getMotion())
                .setVoltage(_event.getVoltage())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

}
