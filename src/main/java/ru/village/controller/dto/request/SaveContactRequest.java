package ru.village.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveContactRequest(
        @NotBlank @Size(max = 100) String type,
        @NotBlank String contactInfo,
        @Size(max = 500) String comment
) {
}
