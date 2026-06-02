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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class PaymentsPageTest extends IntegrationTestBase {

    @Autowired WebApplicationContext ctx;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void paymentsPageOk() throws Exception {
        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(view().name("payments"))
                .andExpect(model().attributeExists("payments"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void filterOffByDefault() throws Exception {
        // без параметров фильтр выключен: year/month == null (показываем все)
        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("year", org.hamcrest.Matchers.nullValue()))
                .andExpect(model().attribute("month", org.hamcrest.Matchers.nullValue()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void pageSizeIsTwenty() throws Exception {
        var result = mockMvc.perform(get("/payments")).andReturn();
        var page = (org.springframework.data.domain.Page<?>) result.getModelAndView().getModel().get("payments");
        org.assertj.core.api.Assertions.assertThat(page.getSize()).isEqualTo(20);
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void operatorSeesEditHint() throws Exception {
        // рендер страницы с #authorization.expression не падает, есть подсказка про клик
        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .content().string(org.hamcrest.Matchers.containsString("Нажмите на запись")));
    }
}
