package ru.village.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.village.IntegrationTestBase;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncoderTest extends IntegrationTestBase {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void encodeAndMatch() {
        String encoded = passwordEncoder.encode("secret");

        assertThat(encoded).isNotEqualTo("secret");
        assertThat(passwordEncoder.matches("secret", encoded)).isTrue();
        assertThat(passwordEncoder.matches("wrong", encoded)).isFalse();
    }
}
