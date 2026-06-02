package ru.village.controller.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Данные поступления для предзаполнения формы редактирования. */
public record PaymentEditDto(
        Long id,
        Long eventId,
        Long householdId,
        String address,
        BigDecimal amount,
        LocalDate date
) {
}
