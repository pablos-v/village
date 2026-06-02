package ru.village.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Создание/правка адреса (домохозяйства). Улица — либо выбор существующей (streetId),
 * либо ввод новой (newStreet); проверяется в сервисе.
 */
public record SaveAddressRequest(
        Long streetId,
        @Size(max = 255) String newStreet,
        @NotBlank @Size(max = 50) String number,
        @Size(max = 255) String description
) {
}
