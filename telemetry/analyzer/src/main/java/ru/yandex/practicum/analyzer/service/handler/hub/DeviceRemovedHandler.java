package ru.yandex.practicum.analyzer.service.handler.hub;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeviceRemovedHandler implements HubEventHandler<DeviceRemovedEventAvro> {
    private final SensorRepository sensorRepository;
    private final String className = this.getClass().getSimpleName();

    @Override
    @Transactional
    public void handleEvent(DeviceRemovedEventAvro data, String hubId) {
        try {
            log.trace("{}: handling DeviceRemovedEventAvro: {}, hubId: {}", className, data, hubId);
            sensorRepository.delete(sensorRepository.findByIdAndHubId(data.getId(), hubId).orElseThrow());
            log.trace("{}: Sensor with id {} and hubId: {} removed", className, data.getId(), hubId);
        } catch (PersistenceException e) {
            log.error("{}: Persistence Error handling DeviceAddedEventAvro: {}", className, e.getMessage());
        } catch (DataAccessException e) {
            log.error("{}: Data Access Error handling DeviceAddedEventAvro: {}", className, e.getMessage());
        } catch (Exception e) {
            log.error("{}: Error handling DeviceAddedEventAvro: {}", className, e.getMessage());
        }

    }

    @Override
    public Class<DeviceRemovedEventAvro> getHandledEventClass() {
        return DeviceRemovedEventAvro.class;
    }
}
