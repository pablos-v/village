package ru.village.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.village.IntegrationTestBase;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class AdminControllerTest extends IntegrationTestBase {

    @Autowired WebApplicationContext ctx;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void eventsListPageOk() throws Exception {
        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events"))
                .andExpect(model().attributeExists("events"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void newEventFormPageOk() throws Exception {
        mockMvc.perform(get("/admin/events/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/event-new"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void createEventRedirects() throws Exception {
        mockMvc.perform(post("/admin/events").with(csrf())
                        .param("name", "2026 май").param("cost", "500"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userForbidden() throws Exception {
        mockMvc.perform(get("/admin/events")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void newPaymentFormPageOk() throws Exception {
        mockMvc.perform(get("/admin/payment/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/payment-new"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("events"));
    }
}
