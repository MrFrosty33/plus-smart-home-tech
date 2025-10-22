package ru.yandex.practicum.shopping.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ShoppingStore {
    //todo конфигурация actuator в application.yml / в конфиге на config-server
    // management.endpoints...

    //todo сделать собственную аннотацию для логгирования методов.
    // посчитать можно также, сколько метод выполнялся по времени
    // https://bytegoblin.io/blog/understanding-spring-aop-creating-annotation-for-logging-requests-and-responses.mdx

    //todo подключить cache: @Cacheable (для добавления), @CachePut (для обновления), @CacheEvict (для удаления)
    // использовать можно Caffeine, настроить его и ограничить хранение в кэше по времени
    public static void main(String[] args) {
        SpringApplication.run(ShoppingStore.class, args);
    }
}
