package ru.yandex.practicum.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.delivery.service.DeliveryService;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Validated
public class DeliveryController {
    private final DeliveryService deliveryService;


}
