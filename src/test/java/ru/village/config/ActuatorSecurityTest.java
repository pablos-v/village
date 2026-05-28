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

/** /actuator/health — публичный; /actuator/info|metrics — только ADMIN. */
class ActuatorSecurityTest extends IntegrationTestBase {

    @Autowired WebApplicationContext ctx;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    @WithAnonymousUser
    void healthIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void metricsAnonymousRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/actuator/metrics")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "USER")
    void metricsForbiddenForUser() throws Exception {
        mockMvc.perform(get("/actuator/metrics")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void metricsForbiddenForOperator() throws Exception {
        mockMvc.perform(get("/actuator/metrics")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void metricsAllowedForAdmin() throws Exception {
        mockMvc.perform(get("/actuator/metrics")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void infoAllowedForAdmin() throws Exception {
        mockMvc.perform(get("/actuator/info")).andExpect(status().isOk());
    }
}
