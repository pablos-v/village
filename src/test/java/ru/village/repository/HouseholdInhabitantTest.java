package ru.village.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.village.IntegrationTestBase;
import ru.village.domain.*;

import static org.assertj.core.api.Assertions.assertThat;

class HouseholdInhabitantTest extends IntegrationTestBase {

    @Autowired StreetRepository streetRepository;
    @Autowired BldngRepository bldngRepository;
    @Autowired AddressRepository addressRepository;
    @Autowired HouseholdRepository householdRepository;
    @Autowired InhabitantRepository inhabitantRepository;

    @Test
    @Transactional
    void householdWithMasterInhabitant() {
        Street street = streetRepository.save(new Street(null, "Зелёная"));
        Bldng bldng = bldngRepository.save(new Bldng(null, "316", null));
        Address address = addressRepository.save(new Address(null, street, bldng));
        Household household = householdRepository.save(new Household(null, address));

        Inhabitant master = new Inhabitant(null, "Иван", "11-11-11", household, true);
        Inhabitant member = new Inhabitant(null, "Анна", "88-88-88", household, false);
        inhabitantRepository.save(master);
        inhabitantRepository.save(member);

        var inhabitants = inhabitantRepository.findByHouseholdId(household.getId());
        assertThat(inhabitants).hasSize(2);
        assertThat(inhabitants).filteredOn(Inhabitant::isMaster).hasSize(1);
    }
}
