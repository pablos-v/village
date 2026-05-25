package ru.village.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Пароли для трёх ролей. Берутся из application.yml (env vars в prod). */
@ConfigurationProperties(prefix = "app.security")
public record AppSecurityProperties(
        String adminPassword,
        String operatorPassword,
        String userPassword
) {
}
