package ru.village.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;

import static org.assertj.core.api.Assertions.assertThat;

class AppSecurityPropertiesTest extends IntegrationTestBase {

    @Autowired
    AppSecurityProperties props;

    @Test
    void propertiesLoaded() {
        assertThat(props.adminPassword()).isNotBlank();
        assertThat(props.operatorPassword()).isNotBlank();
        assertThat(props.userPassword()).isNotBlank();
    }
}
