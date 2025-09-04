package ru.yandex.practicum.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.collector.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.collector.service.AvroKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorEventAvro;

@Component
public class SwitchSensorEventHandler extends BaseSensorEventHandler<SwitchSensorEventAvro> {
    public SwitchSensorEventHandler(AvroKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected SwitchSensorEventAvro mapToAvro(SensorEvent event) {
        SwitchSensorEvent _event = (SwitchSensorEvent) event;
        return SwitchSensorEventAvro.newBuilder()
                .setState(_event.isState())
                .build();
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}
