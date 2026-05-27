package ru.village.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;
import ru.village.domain.*;
import ru.village.repository.*;

import static org.assertj.core.api.Assertions.assertThat;

class HistoryServiceTest extends IntegrationTestBase {

    @Autowired IHistoryService historyService;
    @Autowired StreetRepository streetRepo;
    @Autowired BldngRepository bldngRepo;
    @Autowired AddressRepository addressRepo;
    @Autowired HouseholdRepository hhRepo;
    @Autowired EventRepository eventRepo;
    @Autowired PaymentRepository paymentRepo;
    @Autowired ExpenseRepository expenseRepo;

    @Test
    void periodsAggregatePaymentsAndExpenses() {
        Street st = streetRepo.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepo.save(new Bldng(null, "1", null));
        Household hh = hhRepo.save(new Household(null, addressRepo.save(new Address(null, st, bl))));
        Event ev1 = eventRepo.save(new Event(null, "март", new BigDecimal("500")));
        Event ev2 = eventRepo.save(new Event(null, "апрель", new BigDecimal("500")));

        paymentRepo.save(new Payment(null, hh, LocalDate.now(), ev1, new BigDecimal("500")));
        paymentRepo.save(new Payment(null, hh, LocalDate.now(), ev1, new BigDecimal("300")));
        expenseRepo.save(new Expense(null, ev1, new BigDecimal("200"), LocalDate.now(), ""));
        paymentRepo.save(new Payment(null, hh, LocalDate.now(), ev2, new BigDecimal("400")));

        var periods = historyService.periods();
        // >= 2: помимо созданных здесь событий есть seed-события (Liquibase 006)
        assertThat(periods).hasSizeGreaterThanOrEqualTo(2);
        var march = periods.stream().filter(p -> p.eventName().equals("март")).findFirst().orElseThrow();
        assertThat(march.collected()).isEqualByComparingTo("800.00");
        assertThat(march.spent()).isEqualByComparingTo("200.00");
    }
}
