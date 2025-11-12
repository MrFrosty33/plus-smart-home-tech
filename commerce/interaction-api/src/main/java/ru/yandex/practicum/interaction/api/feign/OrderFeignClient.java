package ru.yandex.practicum.interaction.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.dto.OrderDto;

import java.util.UUID;

@FeignClient(name = "order", fallback = OrderFeignFallback.class)
public interface OrderFeignClient {

    @PostMapping("/api/v1/order/delivery/payment")
    OrderDto paymentOrder(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/payment/failed")
    OrderDto paymentOrderFailed(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/delivery/delivery")
    OrderDto deliveryOrder(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/delivery/failed")
    OrderDto deliveryOrderFailed(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/delivery/assembly")
    OrderDto assemblyOrder(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/assembly/failed")
    OrderDto assemblyOrderFailed(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/delivery/completed")
    OrderDto orderCompleted(@RequestBody UUID orderId);
}
