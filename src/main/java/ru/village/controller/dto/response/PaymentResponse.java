package ru.village.controller.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponse(
        Long id,
        LocalDate date,
        String address,
        String eventName,
        BigDecimal amount
) {
}
