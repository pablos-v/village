package ru.village.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;
import ru.village.domain.Event;
import ru.village.domain.Expense;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseRepositoryTest extends IntegrationTestBase {

    @Autowired EventRepository eventRepository;
    @Autowired ExpenseRepository expenseRepository;

    @Test
    void saveAndFind() {
        Event ev = eventRepository.save(new Event(null, "2026 март", new BigDecimal("500.00")));

        Expense saved = expenseRepository.save(new Expense(
                null, ev, new BigDecimal("5000.00"), LocalDate.of(2026, 3, 10), "Вывоз мусора"));

        assertThat(saved.getId()).isNotNull();
        assertThat(expenseRepository.findById(saved.getId()))
                .get()
                .extracting(Expense::getComment)
                .isEqualTo("Вывоз мусора");
    }
}
