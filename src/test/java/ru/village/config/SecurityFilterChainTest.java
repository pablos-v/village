package ru.village.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.village.IntegrationTestBase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityFilterChainTest extends IntegrationTestBase {

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
    void healthIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void publicPingRequiresLogin() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithAnonymousUser
    void adminPingRequiresLogin() throws Exception {
        mockMvc.perform(get("/admin/events")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void userCanAccessPublic() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void userForbiddenFromAdmin() throws Exception {
        mockMvc.perform(get("/admin/events")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "operator", roles = "OPERATOR")
    void operatorCanAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin/events")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "operator", roles = "OPERATOR")
    void operatorCanAccessPublic() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanAccessEverything() throws Exception {
        mockMvc.perform(get("/admin/events")).andExpect(status().isOk());
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }
}
