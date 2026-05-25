package ru.village.service.ai;

import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Java-слой валидации SQL до отправки в БД. Защита от очевидных атак сверх БД-роли. */
@Component
@Slf4j
public class SqlSecurityValidator {

    private static final Pattern FORBIDDEN = Pattern.compile(
            "\\b(drop|delete|insert|update|alter|create|truncate|grant|revoke|execute|" +
            "merge|copy|vacuum|analyze|cluster|reindex|lock|notify|listen|" +
            "for\\s+update|for\\s+share|into\\s+outfile|information_schema)\\b" +
            "|\\bpg_\\w+" +
            "|;|--|/\\*|\\*/",
            Pattern.CASE_INSENSITIVE
    );

    public void validate(String sql) {
        if (sql == null || sql.isBlank()) {
            throw new SecurityException("Пустой SQL");
        }
        String trimmed = sql.trim();
        if (!trimmed.regionMatches(true, 0, "SELECT", 0, 6)) {
            throw new SecurityException("Разрешены только SELECT");
        }
        if (FORBIDDEN.matcher(trimmed).find()) {
            log.warn("Заблокированный SQL: {}", trimmed);
            throw new SecurityException("Запрещённая конструкция в SQL");
        }
    }
}
