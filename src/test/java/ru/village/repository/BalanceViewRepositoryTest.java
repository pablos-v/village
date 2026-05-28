package ru.village.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;
import ru.village.domain.*;

import static org.assertj.core.api.Assertions.assertThat;

class BalanceViewRepositoryTest extends IntegrationTestBase {

    @Autowired StreetRepository streetRepository;
    @Autowired BldngRepository bldngRepository;
    @Autowired AddressRepository addressRepository;
    @Autowired HouseholdRepository householdRepository;
    @Autowired EventRepository eventRepository;
    @Autowired PaymentRepository paymentRepository;
    @Autowired ExpenseRepository expenseRepository;
    @Autowired BalanceViewRepository balanceViewRepository;

    @Test
    void balanceReflectsPaymentsMinusExpenses() {
        Street st = streetRepository.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepository.save(new Bldng(null, "316", null));
        Address ad = addressRepository.save(new Address(null, st, bl));
        Household hh = householdRepository.save(new Household(null, ad));
        Event ev = eventRepository.save(new Event(null, "2026 март", new BigDecimal("500.00")));

        paymentRepository.save(new Payment(null, hh, LocalDate.of(2026, 3, 5), ev, new BigDecimal("500.00")));
        paymentRepository.save(new Payment(null, hh, LocalDate.of(2026, 3, 6), ev, new BigDecimal("300.00")));
        expenseRepository.save(new Expense(null, ev, new BigDecimal("200.00"), LocalDate.of(2026, 3, 10), "x"));

        BigDecimal balance = balanceViewRepository.findCurrent().getAmount();
        assertThat(balance).isEqualByComparingTo("600.00");  // 500 + 300 - 200
    }
}
