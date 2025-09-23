package ru.yandex.practicum.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorEventAvro;

@Component
public class TemperatureSensorEventHandler extends BaseSensorEventHandler<TemperatureSensorEventAvro> {
    public TemperatureSensorEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected TemperatureSensorEventAvro mapToAvro(SensorEventProto event) {
        TemperatureSensorProto _event = event.getTemperatureSensorEvent();
        return TemperatureSensorEventAvro.newBuilder()
                .setTemperatureC(_event.getTemperatureC())
                .setTemperatureF(_event.getTemperatureF())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }
}
