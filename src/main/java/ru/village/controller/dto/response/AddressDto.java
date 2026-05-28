package ru.village.controller.dto.response;

/** Адрес домохозяйства в формате для UI. label — склеенная строка вида "ул. Зелёная, д. 316". */
public record AddressDto(Long householdId, String label) {
}
