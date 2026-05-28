package ru.village.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.village.IntegrationTestBase;
import ru.village.controller.dto.request.ChatRequest;
import ru.village.service.ai.AiChatService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AiControllerTest extends IntegrationTestBase {

    @Autowired WebApplicationContext ctx;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean AiChatService aiChatService;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCanCallChat() throws Exception {
        when(aiChatService.chat(anyString())).thenReturn("Остаток: 1500 ₽");

        var body = objectMapper.writeValueAsString(new ChatRequest("Какой остаток?"));
        mockMvc.perform(post("/api/ai/chat").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Остаток: 1500 ₽"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void blankMessageRejected() throws Exception {
        var body = objectMapper.writeValueAsString(new ChatRequest(""));
        mockMvc.perform(post("/api/ai/chat").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void anonRedirectedToLogin() throws Exception {
        var body = objectMapper.writeValueAsString(new ChatRequest("hello"));
        mockMvc.perform(post("/api/ai/chat").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().is3xxRedirection());
    }
}
