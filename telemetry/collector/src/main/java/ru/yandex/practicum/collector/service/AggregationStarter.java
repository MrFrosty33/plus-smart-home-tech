package ru.yandex.practicum.collector.service;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AggregationStarter {
    private final KafkaConsumer<Void, SpecificRecordBase> consumer;
    private final KafkaProducer<Void, SpecificRecordBase> producer;
    private final String className = this.getClass().getSimpleName();
    private final JsonMapper jsonMapper;

    // ... объявление полей и конструктора ...
    public AggregationStarter(JsonMapper jsonMapper) {

    }

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */
    public void start() {
        try {

            // ... подготовка к обработке данных ...
            // ... например, подписка на топик ...

            // Цикл обработки событий
            while (true) {
                // ... реализация цикла опроса ...
                // ... и обработка полученных данных ...
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                // Перед тем, как закрыть продюсер и консьюмер, нужно убедится,
                // что все сообщения, лежащие в буффере, отправлены и
                // все оффсеты обработанных сообщений зафиксированы

                // здесь нужно вызвать метод продюсера для сброса данных в буффере
                // здесь нужно вызвать метод консьюмера для фиксиции смещений

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

//    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
//        Проверяем, есть ли снапшот для event.getHubId()
//        Если снапшот есть, то достаём его
//        Если нет, то созадём новый
//
//        Проверяем, есть ли в снапшоте данные для event.getId()
//        Если данные есть, то достаём их в переменную oldState
//        Проверка, если oldState.getTimestamp() произошёл позже, чем
//        event.getTimestamp() или oldState.getData() равен
//        event.getPayload(), то ничего обнавлять не нужно, выходим из метода
//        вернув Optional.empty()
//
//        // если дошли до сюда, значит, пришли новые данные и
//        // снапшот нужно обновить
//        Создаём экземпляр SensorStateAvro на основе данных события
//        Добавляем полученный экземпляр в снапшот
//        Обновляем таймстемп снапшота таймстемпом из события
//        Возвращаем снапшот - Optional.of(snapshot)
//    }
}