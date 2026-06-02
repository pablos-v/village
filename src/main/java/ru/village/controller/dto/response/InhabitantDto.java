package ru.village.controller.dto.response;

/** Житель домохозяйства. */
public record InhabitantDto(
        Long id,
        String name,
        String phone,
        boolean master
) {
}
