package ru.village.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Брендинговые надписи: имя в navbar, слоган в футере, HTML-сниппет счётчика. */
@ConfigurationProperties(prefix = "app.branding")
public record AppBrandingProperties(String brand, String tagline, String counter) {
}
