package ru.yandex.practicum.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"ru.yandex.practicum.payment", "ru.yandex.practicum.interaction.api"})
@EnableFeignClients(basePackages = "ru.yandex.practicum.interaction.api.feign")
@EnableCaching
public class PaymentApp {

    //TODO
    // опционально, после реализации всего:
    // Spring Security - накинуть на gateway, чтобы каждый запрос аутентифицировался, см. QA

    public static void main(String[] args) {
        SpringApplication.run(PaymentApp.class, args);
    }
}
