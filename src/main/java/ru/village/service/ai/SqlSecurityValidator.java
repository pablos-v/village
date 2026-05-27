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

    /**
     * Проверяет SQL и возвращает нормализованный (без хвостовой {@code ;}) запрос.
     * LLM часто добавляет завершающую точку с запятой — её обрезаем ДО проверки,
     * чтобы не путать с {@code ;}-инъекцией в середине (та остаётся запрещённой).
     */
    public String validate(String sql) {
        if (sql == null || sql.isBlank()) {
            throw new SecurityException("Пустой SQL");
        }
        String trimmed = sql.trim();
        if (trimmed.endsWith(";")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1).trim();
        }
        if (!trimmed.regionMatches(true, 0, "SELECT", 0, 6)) {
            throw new SecurityException("Разрешены только SELECT");
        }
        if (FORBIDDEN.matcher(trimmed).find()) {
            log.warn("Заблокированный SQL: {}", trimmed);
            throw new SecurityException("Запрещённая конструкция в SQL");
        }
        return trimmed;
    }
}
