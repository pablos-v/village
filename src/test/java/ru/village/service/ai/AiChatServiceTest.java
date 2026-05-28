package ru.village.service.ai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiChatServiceTest extends IntegrationTestBase {

    @Autowired AiChatService aiChatService;

    /**
     * Тест @Transactional, JPA-данные не закоммичены, а aiJdbcTemplate
     * использует отдельный DataSource — видит только committed.
     * Поэтому проверяем интеграцию через SELECT 1, без таблиц.
     * Запрос к реальным таблицам под village_ai-ролью проверен в AiDataSourceTest.
     */
    @Test
    void executeSqlRunsValidQuery() {
        var rows = aiChatService.executeSql("SELECT 1 AS one");
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).get("one")).isEqualTo(1);
    }

    @Test
    void executeSqlBlocksDelete() {
        assertThatThrownBy(() -> aiChatService.executeSql("DELETE FROM payment"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void executeSqlBlocksPgSleep() {
        assertThatThrownBy(() -> aiChatService.executeSql("SELECT pg_sleep(60)"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void executeSqlBlocksSemicolonInjection() {
        assertThatThrownBy(() -> aiChatService.executeSql("SELECT 1; DROP TABLE payment"))
                .isInstanceOf(SecurityException.class);
    }
}
