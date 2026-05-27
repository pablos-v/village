package ru.village.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;
import ru.village.domain.*;
import ru.village.repository.*;

import static org.assertj.core.api.Assertions.assertThat;

class HouseholdServiceTest extends IntegrationTestBase {

    @Autowired IHouseholdService householdService;
    @Autowired StreetRepository streetRepo;
    @Autowired BldngRepository bldngRepo;
    @Autowired AddressRepository addressRepo;
    @Autowired HouseholdRepository hhRepo;

    @Test
    void searchAddressesByStreetSubstring() {
        Street green = streetRepo.save(new Street(null, "Зелёная"));
        Street blue = streetRepo.save(new Street(null, "Голубая"));
        Bldng b1 = bldngRepo.save(new Bldng(null, "316", null));
        Bldng b2 = bldngRepo.save(new Bldng(null, "5", null));
        hhRepo.save(new Household(null, addressRepo.save(new Address(null, green, b1))));
        hhRepo.save(new Household(null, addressRepo.save(new Address(null, blue, b2))));

        var result = householdService.searchAddresses("зелён");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).label()).isEqualTo("ул. Зелёная, д. 316");
        assertThat(result.get(0).householdId()).isNotNull();
    }

    @Test
    void searchAddressesEmptyQueryReturnsAll() {
        Street st = streetRepo.save(new Street(null, "Сосновая"));
        Bldng bl = bldngRepo.save(new Bldng(null, "1", null));
        hhRepo.save(new Household(null, addressRepo.save(new Address(null, st, bl))));

        assertThat(householdService.searchAddresses("")).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void searchAddressesByHouseNumber() {
        Street green = streetRepo.save(new Street(null, "Зелёная"));
        Street blue = streetRepo.save(new Street(null, "Голубая"));
        hhRepo.save(new Household(null, addressRepo.save(
                new Address(null, green, bldngRepo.save(new Bldng(null, "316", null))))));
        hhRepo.save(new Household(null, addressRepo.save(
                new Address(null, blue, bldngRepo.save(new Bldng(null, "5", null))))));

        // поиск по номеру дома
        var byNumber = householdService.searchAddresses("316");
        assertThat(byNumber).hasSize(1);
        assertThat(byNumber.get(0).label()).isEqualTo("ул. Зелёная, д. 316");

        // поиск по склеенной строке улица+дом
        var byFull = householdService.searchAddresses("зелёная 316");
        assertThat(byFull).hasSize(1);
    }
}
