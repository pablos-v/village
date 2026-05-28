package ru.village;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Базовый класс для интеграционных тестов с реальным PostgreSQL через testcontainers.
 * {@code @Transactional} — каждый тест откатывается, не оставляя данных для следующего.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestBase {

    @SuppressWarnings("resource") // singleton container живёт на всю JVM-сессию, закрывает Ryuk при выходе
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("village")
            .withUsername("village")
            .withPassword("village");

    static {
        postgres.start();
    }

    /** AI DataSource подключается к тому же контейнеру под ролью village_ai (создана Liquibase changeset 005). */
    @DynamicPropertySource
    static void aiDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("app.ai-datasource.url",
                () -> postgres.getJdbcUrl() + "?currentSchema=village");
        registry.add("app.ai-datasource.username", () -> "village_ai");
        registry.add("app.ai-datasource.password", () -> "village_ai_dev");
    }
}
