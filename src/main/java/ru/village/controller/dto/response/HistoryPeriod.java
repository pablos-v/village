package ru.village.controller.dto.response;

import java.math.BigDecimal;

public record HistoryPeriod(
        Long eventId,
        String eventName,
        BigDecimal collected,
        BigDecimal spent
) {
}
