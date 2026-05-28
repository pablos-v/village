package ru.village.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/** DataSource под отдельной БД-ролью village_ai (read-only, schema=village, statement_timeout=3s). */
@Configuration
@EnableConfigurationProperties(AiDataSourceProperties.class)
public class AiDataSourceConfig {

    /**
     * defaultCandidate=false — этот DataSource не должен подхватываться Liquibase/JPA
     * по типу. Они используют main DataSource (auto-configured из spring.datasource.*).
     * Сюда попадаем только через явный @Qualifier("aiDataSource").
     */
    @Bean(defaultCandidate = false)
    public DataSource aiDataSource(AiDataSourceProperties props) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.url());
        config.setUsername(props.username());
        config.setPassword(props.password());
        config.setReadOnly(true);
        config.setMaximumPoolSize(3);
        config.setPoolName("ai-pool");
        return new HikariDataSource(config);
    }

    @Bean(name = "aiJdbcTemplate")
    public JdbcTemplate aiJdbcTemplate(@org.springframework.beans.factory.annotation.Qualifier("aiDataSource") DataSource aiDataSource) {
        return new JdbcTemplate(aiDataSource);
    }
}
