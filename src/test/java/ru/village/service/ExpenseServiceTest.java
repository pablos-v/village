package ru.village.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import ru.village.IntegrationTestBase;
import ru.village.domain.*;
import ru.village.repository.*;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseServiceTest extends IntegrationTestBase {

    @Autowired IExpenseService expenseService;
    @Autowired EventRepository eventRepo;
    @Autowired ExpenseRepository expenseRepo;

    @Test
    void findAllPaged() {
        Event ev = eventRepo.save(new Event(null, "март", new BigDecimal("500")));
        expenseRepo.save(new Expense(null, ev, new BigDecimal("100"), LocalDate.of(2026, 3, 1), "a"));
        expenseRepo.save(new Expense(null, ev, new BigDecimal("200"), LocalDate.of(2026, 3, 5), "b"));

        var page = expenseService.findAll(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent().get(0).date()).isAfterOrEqualTo(page.getContent().get(1).date());
    }
}
