package ru.village.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.village.IntegrationTestBase;
import ru.village.controller.dto.request.CreateEventRequest;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminApiControllerTest extends IntegrationTestBase {

    @Autowired WebApplicationContext ctx;
    @Autowired ObjectMapper objectMapper;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void operatorCreatesEvent() throws Exception {
        var body = objectMapper.writeValueAsString(
                new CreateEventRequest("2026 май", new BigDecimal("500")));
        mockMvc.perform(post("/api/events").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("2026 май"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotCreateEvent() throws Exception {
        var body = objectMapper.writeValueAsString(
                new CreateEventRequest("nope", new BigDecimal("100")));
        mockMvc.perform(post("/api/events").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
    }
}
