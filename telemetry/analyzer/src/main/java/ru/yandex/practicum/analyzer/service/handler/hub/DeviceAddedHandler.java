package ru.yandex.practicum.analyzer.service.handler.hub;

import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeviceAddedHandler implements HubEventHandler<DeviceAddedEventAvro> {
    private final SensorRepository sensorRepository;
    private final String className = this.getClass().getSimpleName();

    @Override
    public void handleEvent(DeviceAddedEventAvro event, String hubId) {
        try {
            log.trace("{}: handling DeviceAddedEventAvro: {}, hubId: {}", className, event, hubId);
            Optional<Sensor> optionalSensor = sensorRepository.findByIdAndHubId(event.getId(), hubId);
            if (optionalSensor.isEmpty()) {
                // содержит лишь 2 поля, id, hubId
                // соответственно если по таким полям не найдена запись - обновлять нечего, остаётся только создать
                Sensor sensor = new Sensor();
                sensor.setId(event.getId());
                sensor.setHubId(hubId);
                sensorRepository.save(sensor);
                log.trace("{}: Sensor created: {}", className, sensor);
            }

        } catch (PersistenceException e) {
            log.error("{}: Persistence Error handling DeviceAddedEventAvro: {}", className, e.getMessage());
        } catch (DataAccessException e) {
            log.error("{}: Data Access Error handling DeviceAddedEventAvro: {}", className, e.getMessage());
        } catch (Exception e) {
            log.error("{}: Error handling DeviceAddedEventAvro: {}", className, e.getMessage());
        }
    }

    @Override
    public Class<DeviceAddedEventAvro> getHandledEventClass() {
        return DeviceAddedEventAvro.class;
    }
}
