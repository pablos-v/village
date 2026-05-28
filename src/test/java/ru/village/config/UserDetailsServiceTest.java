package ru.village.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.village.IntegrationTestBase;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsServiceTest extends IntegrationTestBase {

    @Autowired
    UserDetailsService userDetailsService;

    @Test
    void adminHasAdminRole() {
        UserDetails admin = userDetailsService.loadUserByUsername("admin");
        assertThat(admin.getAuthorities()).extracting("authority")
                .containsExactlyInAnyOrder("ROLE_ADMIN");
    }

    @Test
    void operatorHasOperatorRole() {
        UserDetails op = userDetailsService.loadUserByUsername("operator");
        assertThat(op.getAuthorities()).extracting("authority")
                .containsExactlyInAnyOrder("ROLE_OPERATOR");
    }

    @Test
    void userHasUserRole() {
        UserDetails u = userDetailsService.loadUserByUsername("user");
        assertThat(u.getAuthorities()).extracting("authority")
                .containsExactlyInAnyOrder("ROLE_USER");
    }
}
