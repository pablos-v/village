package ru.village.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.village.IntegrationTestBase;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerTest extends IntegrationTestBase {

    @Autowired WebApplicationContext ctx;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithAnonymousUser
    void welcomeShowsPasswordFieldForResidents() throws Exception {
        mockMvc.perform(get("/welcome"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("Пароль доступа")))
                // витрина подставляет username=user скрытым полем
                .andExpect(content().string(containsString("value=\"user\"")));
    }

    @Test
    @WithAnonymousUser
    void operatorLoginPageHasFullForm() throws Exception {
        mockMvc.perform(get("/login-operator"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Логин")))
                .andExpect(content().string(containsString("Вход для оператора")));
    }
}
