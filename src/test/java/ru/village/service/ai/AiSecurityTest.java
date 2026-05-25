package ru.village.service.ai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Дополнительные security-тесты на AI-эндпоинт. Большая часть атак уже
 * покрыта SqlSecurityValidatorTest и AiDataSourceTest; здесь проверяем
 * многослойную защиту: statement_timeout убивает медленные SELECT'ы,
 * которые validator пропускает.
 */
class AiSecurityTest extends IntegrationTestBase {

    @Autowired AiChatService aiChatService;

    /**
     * Тяжёлый SELECT (не блокируемый validator'ом) должен быть прерван
     * statement_timeout=3s, установленным на роль village_ai.
     */
    @Test
    void heavyQueryKilledByStatementTimeout() {
        assertThatThrownBy(() ->
                aiChatService.executeSql("SELECT COUNT(*) FROM generate_series(1, 1000000000)"))
                .rootCause().hasMessageContaining("statement timeout");
    }

    /** Validator ловит pg_user через regex \bpg_\w+. */
    @Test
    void pgUserBlockedByValidator() {
        assertThatThrownBy(() -> aiChatService.executeSql("SELECT * FROM pg_user"))
                .isInstanceOf(SecurityException.class);
    }

    /** UNION ATTACK: попытка через UNION читать что-то вне village — все не-village схемы недоступны village_ai. */
    @Test
    void unionToInformationSchemaBlocked() {
        assertThatThrownBy(() ->
                aiChatService.executeSql("SELECT 1 UNION SELECT * FROM information_schema.tables"))
                .isInstanceOf(SecurityException.class);
    }
}
