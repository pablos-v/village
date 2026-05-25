package ru.village.controller.dto.response;

import java.math.BigDecimal;

public record EventResponse(Long id, String name, BigDecimal cost) {
}
