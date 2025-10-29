package ru.yandex.practicum.shopping.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"ru.yandex.practicum.shopping.cart", "ru.yandex.practicum.interaction.api"})
@EnableFeignClients(basePackages = "ru.yandex.practicum.interaction.api.feign")
//@EnableCaching
public class ShoppingCartApp {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartApp.class, args);
    }
}
