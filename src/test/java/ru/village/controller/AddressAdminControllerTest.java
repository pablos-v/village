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

class AddressAdminControllerTest extends IntegrationTestBase {

    @Autowired WebApplicationContext ctx;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanListAddresses() throws Exception {
        mockMvc.perform(get("/admin/addresses"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/addresses"))
                .andExpect(model().attributeExists("households"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanOpenNewForm() throws Exception {
        mockMvc.perform(get("/admin/addresses/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/address-form"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("streets"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAddressRedirects() throws Exception {
        mockMvc.perform(post("/admin/addresses").with(csrf())
                        .param("newStreet", "Тестовая").param("number", "42"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/addresses"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAddressWithoutStreetShowsFormError() throws Exception {
        mockMvc.perform(post("/admin/addresses").with(csrf())
                        .param("number", "42"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/address-form"))
                .andExpect(model().attributeHasFieldErrors("form", "newStreet"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void operatorForbidden() throws Exception {
        mockMvc.perform(get("/admin/addresses")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userForbidden() throws Exception {
        mockMvc.perform(get("/admin/addresses")).andExpect(status().isForbidden());
    }
}
