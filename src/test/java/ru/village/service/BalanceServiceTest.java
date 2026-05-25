package ru.village.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;
import ru.village.domain.*;
import ru.village.repository.*;

import static org.assertj.core.api.Assertions.assertThat;

class BalanceServiceTest extends IntegrationTestBase {

    @Autowired IBalanceService balanceService;
    @Autowired StreetRepository streetRepo;
    @Autowired BldngRepository bldngRepo;
    @Autowired AddressRepository addressRepo;
    @Autowired HouseholdRepository hhRepo;
    @Autowired EventRepository eventRepo;
    @Autowired PaymentRepository paymentRepo;
    @Autowired ExpenseRepository expenseRepo;

    @Test
    void balanceIsPaymentsMinusExpenses() {
        Street st = streetRepo.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepo.save(new Bldng(null, "316", null));
        Address ad = addressRepo.save(new Address(null, st, bl));
        Household hh = hhRepo.save(new Household(null, ad));
        Event ev = eventRepo.save(new Event(null, "март", new BigDecimal("500.00")));

        paymentRepo.save(new Payment(null, hh, LocalDate.now(), ev, new BigDecimal("1000.00")));
        expenseRepo.save(new Expense(null, ev, new BigDecimal("300.00"), LocalDate.now(), "x"));

        assertThat(balanceService.currentBalance()).isEqualByComparingTo("700.00");
    }
}
