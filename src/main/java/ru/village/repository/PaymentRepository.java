package ru.village.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.village.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
