package ru.village.controller.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CreateEventRequest(
        @NotBlank String name,
        @DecimalMin(value = "0.01") @DecimalMax(value = "100000") BigDecimal cost
) {
}
