package ru.village.controller.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record CreateExpenseRequest(
        @NotNull Long eventId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull @PastOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @Size(max = 500) String comment
) {
}
