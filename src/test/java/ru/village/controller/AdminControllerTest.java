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
import ru.village.controller.dto.request.CreatePaymentRequest;
import ru.village.domain.*;
import ru.village.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class AdminControllerTest extends IntegrationTestBase {

    @Autowired WebApplicationContext ctx;
    @Autowired EventRepository eventRepo;
    @Autowired StreetRepository streetRepo;
    @Autowired BldngRepository bldngRepo;
    @Autowired AddressRepository addressRepo;
    @Autowired HouseholdRepository hhRepo;
    @Autowired PaymentRepository paymentRepo;
    @Autowired ExpenseRepository expenseRepo;
    @Autowired UsefulContactInfoRepository contactRepo;
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

    @Test
    @WithMockUser(roles = "OPERATOR")
    void newPaymentFormPreselectsDefaultEvent() throws Exception {
        Long expected = eventRepo.findAll().stream()
                .filter(e -> "На общие нужды".equals(e.getName()))
                .findFirst().orElseThrow().getId();

        var result = mockMvc.perform(get("/admin/payment/new")).andReturn();
        var form = (CreatePaymentRequest) result.getModelAndView().getModel().get("form");
        assertThat(form.eventId()).isEqualTo(expected);
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void newExpenseFormPageOk() throws Exception {
        mockMvc.perform(get("/admin/expense/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/expense-new"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("events"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void editPaymentFormPrefillsAddress() throws Exception {
        Street st = streetRepo.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepo.save(new Bldng(null, "316", null));
        Household hh = hhRepo.save(new Household(null, addressRepo.save(new Address(null, st, bl))));
        Event ev = eventRepo.save(new Event(null, "март", new BigDecimal("500")));
        var p = paymentRepo.save(new Payment(null, hh, LocalDate.of(2026, 3, 1), ev, new BigDecimal("500")));

        mockMvc.perform(get("/admin/payment/{id}/edit", p.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/payment-new"))
                .andExpect(model().attribute("formAction", "/admin/payment/" + p.getId()))
                .andExpect(model().attribute("addressLabel", "ул. Зелёная, д. 316"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void updatePaymentRedirectsToPayments() throws Exception {
        Street st = streetRepo.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepo.save(new Bldng(null, "316", null));
        Household hh = hhRepo.save(new Household(null, addressRepo.save(new Address(null, st, bl))));
        Event ev = eventRepo.save(new Event(null, "март", new BigDecimal("500")));
        var p = paymentRepo.save(new Payment(null, hh, LocalDate.of(2026, 3, 1), ev, new BigDecimal("500")));

        mockMvc.perform(post("/admin/payment/{id}", p.getId()).with(csrf())
                        .param("eventId", ev.getId().toString())
                        .param("householdId", hh.getId().toString())
                        .param("amount", "750")
                        .param("date", "2026-04-02"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payments"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void editExpenseFormOk() throws Exception {
        Event ev = eventRepo.save(new Event(null, "март", new BigDecimal("500")));
        var e = expenseRepo.save(new Expense(null, ev, new BigDecimal("100"), LocalDate.of(2026, 3, 1), "c"));

        mockMvc.perform(get("/admin/expense/{id}/edit", e.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/expense-new"))
                .andExpect(model().attribute("formAction", "/admin/expense/" + e.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanAccessContactsList() throws Exception {
        mockMvc.perform(get("/admin/contacts"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/contacts"))
                .andExpect(model().attributeExists("contacts"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void operatorForbiddenFromContacts() throws Exception {
        mockMvc.perform(get("/admin/contacts")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void contactWithEmbeddedPhoneRendersTelLink() throws Exception {
        contactRepo.save(new ru.village.domain.UsefulContactInfo(
                null, "Староста", "Василиса Пупуловна +77788845579", null));
        mockMvc.perform(get("/admin/contacts"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .content().string(org.hamcrest.Matchers.containsString("href=\"tel:+77788845579\"")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanOpenNewContactForm() throws Exception {
        mockMvc.perform(get("/admin/contacts/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/contact-form"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("action", "/admin/contacts"));
    }
}
