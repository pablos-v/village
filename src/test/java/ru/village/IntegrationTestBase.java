package ru.village;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
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

    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("village")
            .withUsername("village")
            .withPassword("village");

    static {
        postgres.start();
    }
}
