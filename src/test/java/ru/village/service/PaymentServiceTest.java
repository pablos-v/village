package ru.village.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import ru.village.IntegrationTestBase;
import ru.village.controller.dto.request.CreatePaymentRequest;
import ru.village.domain.*;
import ru.village.exception.EntityNotFoundException;
import ru.village.repository.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentServiceTest extends IntegrationTestBase {

    @Autowired IPaymentService paymentService;
    @Autowired StreetRepository streetRepo;
    @Autowired BldngRepository bldngRepo;
    @Autowired AddressRepository addressRepo;
    @Autowired HouseholdRepository hhRepo;
    @Autowired EventRepository eventRepo;
    @Autowired PaymentRepository paymentRepo;

    @Test
    void recentReturnsPaymentsOrderedByDateDesc() {
        Street st = streetRepo.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepo.save(new Bldng(null, "316", null));
        Address ad = addressRepo.save(new Address(null, st, bl));
        Household hh = hhRepo.save(new Household(null, ad));
        Event ev = eventRepo.save(new Event(null, "март", new BigDecimal("500.00")));

        paymentRepo.save(new Payment(null, hh, LocalDate.of(2026, 3, 1), ev, new BigDecimal("500")));
        paymentRepo.save(new Payment(null, hh, LocalDate.of(2026, 3, 10), ev, new BigDecimal("500")));
        paymentRepo.save(new Payment(null, hh, LocalDate.of(2026, 3, 5), ev, new BigDecimal("500")));

        var result = paymentService.recent(10);
        assertThat(result).hasSize(3);
        assertThat(result.get(0).date()).isEqualTo(LocalDate.of(2026, 3, 10));
        assertThat(result.get(0).address()).isEqualTo("ул. Зелёная, д. 316");
    }

    @Test
    void findAllPagedFiltersByYearAndMonth() {
        Street st = streetRepo.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepo.save(new Bldng(null, "316", null));
        Household hh = hhRepo.save(new Household(null, addressRepo.save(new Address(null, st, bl))));
        Event ev = eventRepo.save(new Event(null, "x", new BigDecimal("100")));

        paymentRepo.save(new Payment(null, hh, LocalDate.of(2026, 2, 15), ev, new BigDecimal("100")));
        paymentRepo.save(new Payment(null, hh, LocalDate.of(2026, 3, 15), ev, new BigDecimal("100")));

        var page = paymentService.findAll(2026, 3, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).date().getMonthValue()).isEqualTo(3);
    }

    @Test
    void createPayment() {
        Street st = streetRepo.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepo.save(new Bldng(null, "316", null));
        Household hh = hhRepo.save(new Household(null, addressRepo.save(new Address(null, st, bl))));
        Event ev = eventRepo.save(new Event(null, "март", new BigDecimal("500")));

        var resp = paymentService.create(
                new CreatePaymentRequest(ev.getId(), hh.getId(), new BigDecimal("500"), LocalDate.now()));
        assertThat(resp.id()).isNotNull();
        assertThat(resp.amount()).isEqualByComparingTo("500");
    }

    @Test
    void createPaymentFailsOnUnknownEvent() {
        var req = new CreatePaymentRequest(999_999L, 1L, new BigDecimal("500"), LocalDate.now());
        assertThatThrownBy(() -> paymentService.create(req))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
