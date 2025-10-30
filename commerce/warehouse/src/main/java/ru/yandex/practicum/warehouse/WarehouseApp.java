package ru.yandex.practicum.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"ru.yandex.practicum.warehouse", "ru.yandex.practicum.interaction.api"})
@EnableFeignClients(basePackages = "ru.yandex.practicum.interaction.api.feign")
//@EnableCaching
public class WarehouseApp {
    public static void main(String[] args) {
        SpringApplication.run(WarehouseApp.class, args);
    }
}
