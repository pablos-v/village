package ru.village.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/** Конфигурация Spring Security: роли, BCrypt, форма логина, доступы. */
@Configuration
@EnableConfigurationProperties(AppSecurityProperties.class)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder encoder, AppSecurityProperties props) {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin")
                        .password(encoder.encode(props.adminPassword()))
                        .roles("ADMIN")
                        .build(),
                User.withUsername("operator")
                        .password(encoder.encode(props.operatorPassword()))
                        .roles("OPERATOR")
                        .build(),
                User.withUsername("user")
                        .password(encoder.encode(props.userPassword()))
                        .roles("USER")
                        .build()
        );
    }
}
