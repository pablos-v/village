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
import ru.village.controller.dto.request.SaveContactRequest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContactsApiTest extends IntegrationTestBase {

    @Autowired WebApplicationContext ctx;
    @Autowired ObjectMapper objectMapper;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCanReadContacts() throws Exception {
        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateContact() throws Exception {
        var body = objectMapper.writeValueAsString(
                new SaveContactRequest("терапевт", "Иванов +7 (999) 123-45-67", "каб. 312"));
        mockMvc.perform(post("/api/admin/contacts").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void operatorCannotCreateContact() throws Exception {
        var body = objectMapper.writeValueAsString(
                new SaveContactRequest("x", "y", null));
        mockMvc.perform(post("/api/admin/contacts").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotCreateContact() throws Exception {
        var body = objectMapper.writeValueAsString(
                new SaveContactRequest("x", "y", null));
        mockMvc.perform(post("/api/admin/contacts").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
    }
}
