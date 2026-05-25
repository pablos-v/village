package ru.village.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/login", "/css/**", "/js/**", "/webjars/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/admin/contacts/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasAnyRole("OPERATOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/events", "/api/payments", "/api/expenses")
                                .hasAnyRole("OPERATOR", "ADMIN")
                        .anyRequest().hasAnyRole("USER", "OPERATOR", "ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .defaultSuccessUrl("/", true)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout").permitAll()
                )
                .build();
    }
}
