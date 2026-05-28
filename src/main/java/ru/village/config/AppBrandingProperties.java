package ru.village.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Брендинговые надписи: имя в navbar и слоган в футере. */
@ConfigurationProperties(prefix = "app.branding")
public record AppBrandingProperties(String brand, String tagline) {
}
