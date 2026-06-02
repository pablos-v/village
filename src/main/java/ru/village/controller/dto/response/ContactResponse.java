package ru.village.controller.dto.response;

/** contactInfoHtml — contactInfo с номерами +7…, обёрнутыми в кликабельные tel:-ссылки. */
public record ContactResponse(Long id, String type, String contactInfo, String contactInfoHtml, String comment) {
}
