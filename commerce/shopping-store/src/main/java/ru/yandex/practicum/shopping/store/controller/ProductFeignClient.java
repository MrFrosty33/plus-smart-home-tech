package ru.yandex.practicum.shopping.store.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.shopping.store.model.SetProductQuantityStateRequest;

@FeignClient(name = "shopping-store")
@RequestMapping("/api/v1/shopping-store")
public interface ProductFeignClient {
    //todo пока только один маппинг будет использоваться другими сервисами?
    @PostMapping("/quantityState")
    boolean updateQuantityState(@RequestBody SetProductQuantityStateRequest request);
}
