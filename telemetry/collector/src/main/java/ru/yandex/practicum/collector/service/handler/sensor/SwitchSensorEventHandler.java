package ru.yandex.practicum.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorEventAvro;

@Component
public class SwitchSensorEventHandler extends BaseSensorEventHandler<SwitchSensorEventAvro> {
    public SwitchSensorEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected SwitchSensorEventAvro mapToAvro(SensorEventProto event) {
        SwitchSensorProto _event = event.getSwitchSensorEvent();
        return SwitchSensorEventAvro.newBuilder()
                .setState(_event.getState())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }
}
