package ru.village.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import ru.village.IntegrationTestBase;
import ru.village.controller.dto.request.CreateExpenseRequest;
import ru.village.domain.*;
import ru.village.exception.InsufficientBalanceException;
import ru.village.repository.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpenseServiceTest extends IntegrationTestBase {

    @Autowired IExpenseService expenseService;
    @Autowired EventRepository eventRepo;
    @Autowired ExpenseRepository expenseRepo;
    @Autowired StreetRepository streetRepo;
    @Autowired BldngRepository bldngRepo;
    @Autowired AddressRepository addressRepo;
    @Autowired HouseholdRepository hhRepo;
    @Autowired PaymentRepository paymentRepo;

    @Test
    void findAllPaged() {
        Event ev = eventRepo.save(new Event(null, "март", new BigDecimal("500")));
        expenseRepo.save(new Expense(null, ev, new BigDecimal("100"), LocalDate.of(2026, 3, 1), "a"));
        expenseRepo.save(new Expense(null, ev, new BigDecimal("200"), LocalDate.of(2026, 3, 5), "b"));

        var page = expenseService.findAll(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent().get(0).date()).isAfterOrEqualTo(page.getContent().get(1).date());
    }

    @Test
    void createExpenseSucceedsWhenWithinBalance() {
        Event ev = eventRepo.save(new Event(null, "x", new BigDecimal("500")));
        Street st = streetRepo.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepo.save(new Bldng(null, "1", null));
        Household hh = hhRepo.save(new Household(null, addressRepo.save(new Address(null, st, bl))));
        paymentRepo.save(new Payment(null, hh, LocalDate.now(), ev, new BigDecimal("100")));

        var resp = expenseService.create(
                new CreateExpenseRequest(ev.getId(), new BigDecimal("50"), LocalDate.now(), "y"));
        assertThat(resp.id()).isNotNull();
    }

    @Test
    void createExpenseFailsWhenAmountExceedsBalance() {
        Event ev = eventRepo.save(new Event(null, "x", new BigDecimal("100")));
        Street st = streetRepo.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepo.save(new Bldng(null, "1", null));
        Household hh = hhRepo.save(new Household(null, addressRepo.save(new Address(null, st, bl))));
        paymentRepo.save(new Payment(null, hh, LocalDate.now(), ev, new BigDecimal("100")));

        var req = new CreateExpenseRequest(ev.getId(), new BigDecimal("200"), LocalDate.now(), "x");
        assertThatThrownBy(() -> expenseService.create(req))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    void updateExpenseChangesFields() {
        Event ev = eventRepo.save(new Event(null, "x", new BigDecimal("500")));
        Street st = streetRepo.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepo.save(new Bldng(null, "1", null));
        Household hh = hhRepo.save(new Household(null, addressRepo.save(new Address(null, st, bl))));
        paymentRepo.save(new Payment(null, hh, LocalDate.now(), ev, new BigDecimal("1000")));
        var exp = expenseRepo.save(new Expense(null, ev, new BigDecimal("100"), LocalDate.now(), "old"));

        var dto = expenseService.getForEdit(exp.getId());
        assertThat(dto.amount()).isEqualByComparingTo("100");

        expenseService.update(exp.getId(),
                new CreateExpenseRequest(ev.getId(), new BigDecimal("300"), LocalDate.now(), "new"));

        var reloaded = expenseRepo.findById(exp.getId()).orElseThrow();
        assertThat(reloaded.getAmount()).isEqualByComparingTo("300");
        assertThat(reloaded.getComment()).isEqualTo("new");
    }

    @Test
    void updateExpenseAllowsRaisingUpToOldAmountPlusBalance() {
        // остаток после: приход 100 − расход 100 = 0. Правка расхода до 100 (=available) — ок.
        Event ev = eventRepo.save(new Event(null, "x", new BigDecimal("500")));
        Street st = streetRepo.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepo.save(new Bldng(null, "1", null));
        Household hh = hhRepo.save(new Household(null, addressRepo.save(new Address(null, st, bl))));
        paymentRepo.save(new Payment(null, hh, LocalDate.now(), ev, new BigDecimal("100")));
        var exp = expenseRepo.save(new Expense(null, ev, new BigDecimal("100"), LocalDate.now(), "x"));

        // попытка поднять до 150 при доступном 100 → запрет
        var tooMuch = new CreateExpenseRequest(ev.getId(), new BigDecimal("150"), LocalDate.now(), "x");
        assertThatThrownBy(() -> expenseService.update(exp.getId(), tooMuch))
                .isInstanceOf(InsufficientBalanceException.class);
    }
}
