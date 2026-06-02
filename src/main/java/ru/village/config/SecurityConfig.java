package ru.village.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

/** Конфигурация Spring Security: роли, BCrypt, форма логина, доступы. */
@Configuration
@EnableConfigurationProperties(AppSecurityProperties.class)
public class SecurityConfig {

    /** Маркер-логин с витрины /welcome: роль подбирается по введённому паролю. */
    private static final String BY_PASSWORD = "__by_password__";

    /** Учётки, против которых сверяется пароль с витрины (по убыванию прав). */
    private static final List<String> KIOSK_CANDIDATES = List.of("admin", "operator", "user");

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

    /**
     * Провайдер аутентификации. С витрины /welcome приходит маркер {@link #BY_PASSWORD} —
     * роль (admin/operator/user) подбирается по совпадению введённого пароля.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService uds, PasswordEncoder encoder) {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) {
                String username = authentication.getName();
                String rawPassword = authentication.getCredentials().toString();

                if (BY_PASSWORD.equals(username)) {
                    for (String candidate : KIOSK_CANDIDATES) {
                        UserDetails u = uds.loadUserByUsername(candidate);
                        if (encoder.matches(rawPassword, u.getPassword())) {
                            return authToken(u);
                        }
                    }
                    throw new BadCredentialsException("Неверный пароль");
                }

                UserDetails u = uds.loadUserByUsername(username);
                if (!encoder.matches(rawPassword, u.getPassword())) {
                    throw new BadCredentialsException("Неверный пароль");
                }
                return authToken(u);
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }

            private Authentication authToken(UserDetails u) {
                return new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities());
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        return http
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/welcome",
                                "/css/**", "/js/**", "/webjars/**").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/admin/contacts/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasAnyRole("OPERATOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/events", "/api/payments", "/api/expenses")
                                .hasAnyRole("OPERATOR", "ADMIN")
                        .anyRequest().hasAnyRole("USER", "OPERATOR", "ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/welcome")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/welcome?logout").permitAll()
                )
                .build();
    }
}
