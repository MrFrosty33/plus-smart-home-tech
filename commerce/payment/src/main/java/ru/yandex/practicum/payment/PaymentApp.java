package ru.yandex.practicum.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"ru.yandex.practicum.payment", "ru.yandex.practicum.interaction.api"})
@EnableFeignClients(basePackages = "ru.yandex.practicum.interaction.api.feign")
public class PaymentApp {

    //TODO
    // опционально, после реализации всего:
    // cache
    // Spring Security - накинуть на gateway, чтобы каждый запрос аутентифицировался, см. QA

    public static void main(String[] args) {
        SpringApplication.run(PaymentApp.class, args);
    }
}
