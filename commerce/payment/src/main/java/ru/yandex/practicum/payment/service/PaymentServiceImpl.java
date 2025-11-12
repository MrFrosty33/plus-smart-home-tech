package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.OrderDto;
import ru.yandex.practicum.interaction.api.dto.PaymentDto;
import ru.yandex.practicum.interaction.api.dto.PaymentState;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.exception.InternalServerException;
import ru.yandex.practicum.interaction.api.exception.NotEnoughInfoToCalculateException;
import ru.yandex.practicum.interaction.api.exception.NotFoundException;
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

    private final CacheManager cacheManager;


    @Override
    @Transactional
    @Loggable
    @CachePut(cacheNames = "payment.payments", key = "result.paymentId")
    public PaymentDto createPayment(OrderDto orderDto) {
        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .productTotal(orderDto.getProductsPrice())
                .deliveryTotal(orderDto.getDeliveryPrice())
                .totalPayment(orderDto.getTotalPrice())
                .paymentState(PaymentState.PENDING)
                .build();

        paymentRepository.save(payment);

        return paymentMapper.toDto(payment);
    }

    @Override
    @Loggable
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        try {
            BigDecimal result = BigDecimal.ZERO;
            result = result.setScale(2, RoundingMode.UP);

            result = result.add(orderDto.getProductsPrice().multiply(BigDecimal.valueOf(1.1)));

            result = result.add(orderDto.getDeliveryPrice());

            return result;
        } catch (NullPointerException e) {
            log.warn("{}: failure in calculateTotalCost(), not enough data to calculate, orderDto: {}", className, orderDto);
            String message = "Not enough data to calculate total cost";
            String userMessage = "Not enough data";
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new NotEnoughInfoToCalculateException(message, userMessage, status);
        }
    }

    @Override
    @Loggable
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        try {
            AtomicReference<BigDecimal> productCostResult = new AtomicReference<>(BigDecimal.ZERO);
            productCostResult.set(productCostResult.get().setScale(2, RoundingMode.UP));

            log.info("{}: initial productCost value: {}", className, productCostResult.get());

            orderDto.getProducts().forEach((key, value) -> {
                ProductDto product = shoppingStoreFeignClient.getById(key);
                productCostResult.set(productCostResult.get().add(product.getPrice().multiply(BigDecimal.valueOf(value))));
            });
            log.info("{}: productCost value after calculating products price: {}", className, productCostResult.get());

            return productCostResult.get();
        } catch (NullPointerException e) {
            log.warn("{}: failure in calculateProductCost(), not enough data to calculate, orderDto: {}", className, orderDto);
            String message = "Not enough data to calculate product cost";
            String userMessage = "Not enough data";
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new NotEnoughInfoToCalculateException(message, userMessage, status);
        }
    }

    @Override
    @Transactional
    @Loggable
    public void paymentSuccess(UUID paymentId) {
        Payment payment = findPaymentInCacheOrDb(paymentId);

        payment.setPaymentState(PaymentState.SUCCESS);

        paymentRepository.save(payment);
        cacheManager.getCache("payment.payments").put(paymentId, paymentMapper.toDto(payment));

        log.info("{}: update payment with id: {}, new status: {}",
                className, paymentId, payment.getPaymentState());

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

        paymentRepository.save(payment);
        cacheManager.getCache("payment.payments").put(paymentId, paymentMapper.toDto(payment));

        log.info("{}: update payment with id: {}, new status: {}",
                className, paymentId, payment.getPaymentState());

        OrderDto payedOrder = orderFeignClient.paymentOrderFailed(payment.getOrderId());
        if (payedOrder == null) {
            log.warn("{}: orderFeignClient is unavailable — payment failed request did not reach its destination.", className);
            String message = "Order feignClient not available";
            throw new InternalServerException(message);
        }
    }

    private Payment findPaymentInCacheOrDb(UUID paymentId) {
        Cache.ValueWrapper valueWrapper = cacheManager.getCache("payment.payments").get(paymentId);
        Payment payment;

        if (valueWrapper != null) {
            payment = paymentMapper.toEntity((PaymentDto) valueWrapper.get());
            log.info("{}: found Payment in cache", className);
        } else {
            payment = paymentRepository.findById(paymentId).orElseThrow(() -> {
                log.warn("{}: no Payment found for paymentId: {}", className, paymentId);
                String message = "Payment for paymentId: " + paymentId + " cannot be found";
                String userMessage = "Payment not found";
                HttpStatus status = HttpStatus.NOT_FOUND;
                return new NotFoundException(message, userMessage, status);
            });
            log.info("{}: found Payment in DB", className);
        }

        return payment;
    }
}
