package ru.yandex.practicum.interaction.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.dto.OrderDto;

import java.util.UUID;

@FeignClient(name = "order", fallback = OrderFeignFallback.class)
public interface OrderFeignClient {

    @PostMapping("/api/v1/payment/failed")
    OrderDto paymentFailed(@RequestBody UUID orderId);

    @PostMapping("/api/v1/delivery/failed")
    OrderDto deliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/api/v1/assembly/failed")
    OrderDto assemblyFailed(@RequestBody UUID orderId);
}
