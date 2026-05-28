package ru.village.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import ru.village.controller.dto.request.CreateExpenseRequest;
import ru.village.controller.dto.request.CreatePaymentRequest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest extends IntegrationTestBase {

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
    void illegalArgumentReturns400WithJsonBody() throws Exception {
        mockMvc.perform(get("/api/payments/recent?limit=-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("bad_request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void validationFailureReturns400() throws Exception {
        // cost = -1 нарушает @DecimalMin("0.01")
        var body = objectMapper.writeValueAsString(
                new CreateEventRequest("test", new BigDecimal("-1")));
        mockMvc.perform(post("/api/events").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void entityNotFoundReturns404() throws Exception {
        // несуществующий eventId
        var body = objectMapper.writeValueAsString(
                new CreatePaymentRequest(999_999L, 999_999L, new BigDecimal("100"), LocalDate.now()));
        mockMvc.perform(post("/api/payments").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void insufficientBalanceReturns409() throws Exception {
        // создаём event без поступлений → баланс 0, расход 100 → 409
        var eventBody = objectMapper.writeValueAsString(
                new CreateEventRequest("for-overdraw", new BigDecimal("500")));
        String evResp = mockMvc.perform(post("/api/events").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(eventBody))
                .andReturn().getResponse().getContentAsString();
        long eventId = objectMapper.readTree(evResp).get("id").asLong();

        var expenseBody = objectMapper.writeValueAsString(
                new CreateExpenseRequest(eventId, new BigDecimal("100"), LocalDate.now(), "overdraw"));
        mockMvc.perform(post("/api/expenses").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(expenseBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("insufficient_balance"));
    }
}
