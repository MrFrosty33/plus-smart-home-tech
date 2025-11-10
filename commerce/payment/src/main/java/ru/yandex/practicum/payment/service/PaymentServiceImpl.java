package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.PaymentDto;
import ru.yandex.practicum.interaction.api.dto.PaymentState;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.exception.InternalServerException;
import ru.yandex.practicum.interaction.api.exception.NoDeliveryFoundException;
import ru.yandex.practicum.interaction.api.feign.OrderFeignClient;
import ru.yandex.practicum.interaction.api.feign.ShoppingStoreFeignClient;
import ru.yandex.practicum.interaction.api.logging.Loggable;
import ru.yandex.practicum.payment.mapper.PaymentMapper;
import ru.yandex.practicum.payment.model.Payment;
import ru.yandex.practicum.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final String className = this.getClass().getSimpleName();
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    private final ShoppingStoreFeignClient shoppingStoreFeignClient;
    private final OrderFeignClient orderFeignClient;


    @Override
    @Transactional
    @Loggable
    public PaymentDto createPayment(OrderDto orderDto) {
        return null;
    }

    @Override
    @Loggable
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        BigDecimal result = BigDecimal.ZERO;
        result = result.setScale(2, RoundingMode.UP);

        result = result.add(orderDto.getProductsPrice().multiply(BigDecimal.valueOf(1.1)));

        result = result.add(orderDto.getDeliveryPrice());

        return result;
    }

    @Override
    @Loggable
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        AtomicReference<BigDecimal> result = new AtomicReference<>(BigDecimal.ZERO);
        result.set(result.get().setScale(2, RoundingMode.UP));

        orderDto.getProducts().forEach((key, value) -> {
            ProductDto product = shoppingStoreFeignClient.getById(key);
            result.set(result.get().add(product.getPrice().multiply(BigDecimal.valueOf(value))));
        });

        return result.get();
    }

    @Override
    @Transactional
    @Loggable
    public void paymentSuccess(UUID paymentId) {
        Payment payment = findPaymentInCacheOrDb(paymentId);

        payment.setPaymentState(PaymentState.SUCCESS);

        OrderDto payedOrder = orderFeignClient.paymentOrder(payment.getOrderId());
        if (payedOrder == null) {
            log.warn("{}: orderFeignClient is unavailable — payment success request did not reach its destination.", className);
            String message = "Order feignClient not available";
            throw new InternalServerException(message);
        }
    }

    @Override
    @Transactional
    @Loggable
    public void paymentFailed(UUID paymentId) {
        Payment payment = findPaymentInCacheOrDb(paymentId);

        payment.setPaymentState(PaymentState.FAILED);

        OrderDto payedOrder = orderFeignClient.paymentOrderFailed(payment.getOrderId());
        if (payedOrder == null) {
            log.warn("{}: orderFeignClient is unavailable — payment failed request did not reach its destination.", className);
            String message = "Order feignClient not available";
            throw new InternalServerException(message);
        }
    }

    private Payment findPaymentInCacheOrDb(UUID paymentId) {
        //todo cache
        return paymentRepository.findById(paymentId).orElseThrow(() -> {
            log.warn("{}: no Payment found for paymentId: {}", className, paymentId);
            String message = "Payment for paymentId: " + paymentId + " cannot be found";
            String userMessage = "Payment not found";
            HttpStatus status = HttpStatus.NOT_FOUND;
            return new NoDeliveryFoundException(message, userMessage, status);
        });
    }
}
