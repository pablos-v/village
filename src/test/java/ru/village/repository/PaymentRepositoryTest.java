package ru.village.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.village.IntegrationTestBase;
import ru.village.domain.*;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentRepositoryTest extends IntegrationTestBase {

    @Autowired StreetRepository streetRepository;
    @Autowired BldngRepository bldngRepository;
    @Autowired AddressRepository addressRepository;
    @Autowired HouseholdRepository householdRepository;
    @Autowired EventRepository eventRepository;
    @Autowired PaymentRepository paymentRepository;

    @Test
    @Transactional
    void saveWithRelations() {
        Street st = streetRepository.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepository.save(new Bldng(null, "316", null));
        Address ad = addressRepository.save(new Address(null, st, bl));
        Household hh = householdRepository.save(new Household(null, ad));
        Event ev = eventRepository.save(new Event(null, "2026 март", new BigDecimal("500.00")));

        Payment payment = paymentRepository.save(new Payment(
                null, hh, LocalDate.of(2026, 3, 5), ev, new BigDecimal("500.00")));

        assertThat(payment.getId()).isNotNull();
        assertThat(paymentRepository.findById(payment.getId()))
                .get()
                .satisfies(p -> {
                    assertThat(p.getAmount()).isEqualTo(new BigDecimal("500.00"));
                    assertThat(p.getEvent().getName()).isEqualTo("2026 март");
                });
    }
}
