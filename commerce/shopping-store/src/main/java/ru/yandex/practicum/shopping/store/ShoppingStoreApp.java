package ru.yandex.practicum.shopping.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = {"ru.yandex.practicum.shopping.store", "ru.yandex.practicum.interaction.api"})
@EnableCaching
public class ShoppingStoreApp {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingStoreApp.class, args);
    }
}
