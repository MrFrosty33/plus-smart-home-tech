package ru.yandex.practicum.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"ru.yandex.practicum.payment", "ru.yandex.practicum.interaction.api"})
@EnableFeignClients(basePackages = "ru.yandex.practicum.interaction.api.feign")
public class PaymentApp {

    //TODO
    // в спецификациях есть неточности. В некоторых эндпоинтах можно запрашивать будет допольнительные параметры

    //TODO
    // не забыть добавить новые БД и таблицы в db/schema.sql

    //TODO
    // опционально, после реализации всего:
    // cache
    // Spring Security - накинуть на gateway, чтобы каждый запрос аутентифицировался, см. QA

    public static void main(String[] args) {
        SpringApplication.run(PaymentApp.class, args);
    }
}
