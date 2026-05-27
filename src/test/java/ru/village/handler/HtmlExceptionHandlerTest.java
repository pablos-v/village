package ru.village.handler;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.village.IntegrationTestBase;
import ru.village.domain.Event;
import ru.village.repository.EventRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class HtmlExceptionHandlerTest extends IntegrationTestBase {

    @Autowired WebApplicationContext ctx;
    @Autowired EventRepository eventRepo;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    /**
     * POST /admin/expense с суммой больше остатка (баланс 0) → InsufficientBalanceException.
     * HtmlExceptionHandler должен вернуть HTML-страницу error, а не JSON.
     */
    @Test
    @WithMockUser(roles = "OPERATOR")
    void insufficientBalanceRendersErrorPage() throws Exception {
        Event ev = eventRepo.save(new Event(null, "март", new BigDecimal("500")));

        mockMvc.perform(post("/admin/expense").with(csrf())
                        .param("eventId", ev.getId().toString())
                        .param("amount", "100")
                        .param("date", java.time.LocalDate.now().toString())
                        .param("comment", "overdraw"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }
}
