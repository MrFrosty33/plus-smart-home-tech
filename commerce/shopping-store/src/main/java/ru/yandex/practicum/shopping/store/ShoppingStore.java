package ru.yandex.practicum.shopping.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@ConfigurationPropertiesScan
public class ShoppingStore {
    //todo конфигурация actuator в application.yml / в конфиге на config-server
    // management.endpoints...
    // а также правильная ссылка на БД
    public static void main(String[] args) {
        SpringApplication.run(ShoppingStore.class, args);
    }
}
