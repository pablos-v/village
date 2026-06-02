package ru.village.controller.dto.response;

/** Данные адреса (дома) для предзаполнения формы редактирования. */
public record HouseholdEditDto(
        Long id,
        Long streetId,
        String number,
        String description,
        String addressLabel
) {
}
