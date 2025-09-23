package ru.yandex.practicum.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorEventAvro;

@Component
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorEventAvro> {
    public LightSensorEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected LightSensorEventAvro mapToAvro(SensorEventProto event) {
        LightSensorProto _event = event.getLightSensorEvent();
        return LightSensorEventAvro.newBuilder()
                .setLinkQuality(_event.getLinkQuality())
                .setLuminosity(_event.getLuminosity())
                .build();

    }

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }
}
