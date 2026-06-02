package ru.village.controller.dto.response;

/** Строка списка адресов в админке: id дома, полный адрес, число жителей. */
public record HouseholdAdminDto(
        Long id,
        String address,
        long inhabitants
) {
}
