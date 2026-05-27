package ru.village.controller.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record CreatePaymentRequest(
        @NotNull Long eventId,
        @NotNull Long householdId,
        @NotNull @DecimalMin("0.01") @DecimalMax("10000") BigDecimal amount,
        @NotNull @PastOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
) {
}
