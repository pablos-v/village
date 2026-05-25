package ru.village.controller.dto.response;

public record ContactResponse(Long id, String type, String contactInfo, String comment) {
}
