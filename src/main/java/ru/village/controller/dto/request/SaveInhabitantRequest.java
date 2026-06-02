package ru.village.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Создание/правка жителя домохозяйства. */
public record SaveInhabitantRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 50) String phone,
        boolean master
) {
}
