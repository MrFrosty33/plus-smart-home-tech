package ru.yandex.practicum.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.payment.mapper.PaymentMapper;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentMapper, UUID> {
}
