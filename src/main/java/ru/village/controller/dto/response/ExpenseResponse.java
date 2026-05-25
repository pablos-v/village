package ru.village.controller.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(
        Long id,
        LocalDate date,
        String comment,
        String eventName,
        BigDecimal amount
) {
}
