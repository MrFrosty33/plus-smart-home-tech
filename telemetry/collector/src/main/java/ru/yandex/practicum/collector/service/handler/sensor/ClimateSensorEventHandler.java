package ru.yandex.practicum.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorEventAvro;

@Component
public class ClimateSensorEventHandler extends BaseSensorEventHandler<ClimateSensorEventAvro> {

    public ClimateSensorEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected ClimateSensorEventAvro mapToAvro(SensorEventProto event) {
        ClimateSensorProto _event = event.getClimateSensorEvent();
        return ClimateSensorEventAvro.newBuilder()
                .setTemperatureC(_event.getTemperatureC())
                .setHumidity(_event.getHumidity())
                .setCo2Level(_event.getCo2Level())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }
}
