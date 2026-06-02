package ru.village.controller.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Данные расхода для предзаполнения формы редактирования. */
public record ExpenseEditDto(
        Long id,
        Long eventId,
        BigDecimal amount,
        LocalDate date,
        String comment
) {
}
