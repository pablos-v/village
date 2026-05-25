package ru.village.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.village.IntegrationTestBase;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

class AuthenticationE2ETest extends IntegrationTestBase {

    @Autowired WebApplicationContext ctx;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void validLoginAuthenticatesAsAdmin() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("admin").password("admin"))
                .andExpect(authenticated().withRoles("ADMIN"));
    }

    @Test
    void validLoginAuthenticatesAsOperator() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("operator").password("operator"))
                .andExpect(authenticated().withRoles("OPERATOR"));
    }

    @Test
    void validLoginAuthenticatesAsUser() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("user").password("user"))
                .andExpect(authenticated().withRoles("USER"));
    }

    @Test
    void invalidPasswordFailsAuthentication() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("admin").password("wrong"))
                .andExpect(unauthenticated());
    }
}
