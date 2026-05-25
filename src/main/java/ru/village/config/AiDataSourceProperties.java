package ru.village.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Параметры DataSource'а под ролью village_ai (read-only, schema=village). */
@ConfigurationProperties(prefix = "app.ai-datasource")
public record AiDataSourceProperties(String url, String username, String password) {
}
