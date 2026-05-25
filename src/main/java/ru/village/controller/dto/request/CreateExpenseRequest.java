package ru.village.controller.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateExpenseRequest(
        @NotNull Long eventId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull @PastOrPresent LocalDate date,
        @Size(max = 500) String comment
) {
}
