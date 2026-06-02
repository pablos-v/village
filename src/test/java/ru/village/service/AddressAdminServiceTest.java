package ru.village.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;
import ru.village.controller.dto.request.SaveAddressRequest;
import ru.village.controller.dto.request.SaveInhabitantRequest;
import ru.village.domain.*;
import ru.village.exception.AddressInUseException;
import ru.village.repository.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressAdminServiceTest extends IntegrationTestBase {

    @Autowired IAddressAdminService service;
    @Autowired StreetRepository streetRepo;
    @Autowired BldngRepository bldngRepo;
    @Autowired AddressRepository addressRepo;
    @Autowired HouseholdRepository hhRepo;
    @Autowired InhabitantRepository inhabitantRepo;
    @Autowired EventRepository eventRepo;
    @Autowired PaymentRepository paymentRepo;

    @Test
    void createWithNewStreet() {
        service.create(new SaveAddressRequest(null, "Новая", "10", "описание"));

        var found = service.list().stream()
                .filter(h -> h.address().equals("ул. Новая, д. 10")).findFirst();
        assertThat(found).isPresent();
        assertThat(streetRepo.findByNameIgnoreCase("Новая")).isPresent();
    }

    @Test
    void createWithExistingStreetReuses() {
        Street st = streetRepo.save(new Street(null, "Сосновая"));
        service.create(new SaveAddressRequest(st.getId(), "", "7", null));

        long sameName = streetRepo.findAllByOrderByNameAsc().stream()
                .filter(s -> s.getName().equals("Сосновая")).count();
        assertThat(sameName).isEqualTo(1);
    }

    @Test
    void updateChangesNumberAndStreet() {
        Street a = streetRepo.save(new Street(null, "Первая"));
        Street b = streetRepo.save(new Street(null, "Вторая"));
        Bldng bl = bldngRepo.save(new Bldng(null, "1", null));
        Household hh = hhRepo.save(new Household(null, addressRepo.save(new Address(null, a, bl))));

        service.update(hh.getId(), new SaveAddressRequest(b.getId(), "", "99", null));

        var dto = service.getForEdit(hh.getId());
        assertThat(dto.streetId()).isEqualTo(b.getId());
        assertThat(dto.number()).isEqualTo("99");
    }

    @Test
    void deleteBlockedWhenPaymentsExist() {
        Street st = streetRepo.save(new Street(null, "Зелёная"));
        Bldng bl = bldngRepo.save(new Bldng(null, "5", null));
        Household hh = hhRepo.save(new Household(null, addressRepo.save(new Address(null, st, bl))));
        Event ev = eventRepo.save(new Event(null, "x", new BigDecimal("100")));
        paymentRepo.save(new Payment(null, hh, LocalDate.now(), ev, new BigDecimal("100")));

        assertThatThrownBy(() -> service.delete(hh.getId()))
                .isInstanceOf(AddressInUseException.class);
        assertThat(hhRepo.existsById(hh.getId())).isTrue();
    }

    @Test
    void deleteRemovesHouseholdAndInhabitants() {
        Street st = streetRepo.save(new Street(null, "Берёзовая"));
        Bldng bl = bldngRepo.save(new Bldng(null, "3", null));
        Household hh = hhRepo.save(new Household(null, addressRepo.save(new Address(null, st, bl))));
        inhabitantRepo.save(new Inhabitant(null, "Иван", null, hh, true));

        service.delete(hh.getId());

        assertThat(hhRepo.existsById(hh.getId())).isFalse();
        assertThat(inhabitantRepo.findByHouseholdId(hh.getId())).isEmpty();
    }

    @Test
    void inhabitantCrud() {
        Street st = streetRepo.save(new Street(null, "Луговая"));
        Bldng bl = bldngRepo.save(new Bldng(null, "2", null));
        Household hh = hhRepo.save(new Household(null, addressRepo.save(new Address(null, st, bl))));

        service.addInhabitant(hh.getId(), new SaveInhabitantRequest("Пётр", "111", true));
        var detail = service.detail(hh.getId());
        assertThat(detail.inhabitants()).hasSize(1);
        assertThat(detail.inhabitants().get(0).master()).isTrue();

        Long iid = detail.inhabitants().get(0).id();
        service.updateInhabitant(iid, new SaveInhabitantRequest("Пётр Иванов", "222", false));
        assertThat(inhabitantRepo.findById(iid).orElseThrow().getName()).isEqualTo("Пётр Иванов");

        service.deleteInhabitant(iid);
        assertThat(service.detail(hh.getId()).inhabitants()).isEmpty();
    }
}
