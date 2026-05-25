package ru.village.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.village.IntegrationTestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiDataSourceTest extends IntegrationTestBase {

    @Autowired
    @Qualifier("aiJdbcTemplate")
    JdbcTemplate aiJdbc;

    @Test
    void canSelectFromVillageSchema() {
        Integer count = aiJdbc.queryForObject("SELECT COUNT(*) FROM payment", Integer.class);
        assertThat(count).isNotNull();
    }

    @Test
    void cannotInsert() {
        assertThatThrownBy(() ->
                aiJdbc.update("INSERT INTO events (name, cost) VALUES ('hack', 999)")
        ).rootCause().hasMessageContaining("permission denied");
    }

    @Test
    void cannotDelete() {
        assertThatThrownBy(() -> aiJdbc.update("DELETE FROM payment"))
                .rootCause().hasMessageContaining("permission denied");
    }

    @Test
    void cannotCreateTable() {
        assertThatThrownBy(() -> aiJdbc.update("CREATE TABLE hack (id int)"))
                .rootCause().hasMessageContaining("permission denied");
    }
}
