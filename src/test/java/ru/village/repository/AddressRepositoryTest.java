package ru.village.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.village.IntegrationTestBase;
import ru.village.domain.Address;
import ru.village.domain.Bldng;
import ru.village.domain.Street;

import static org.assertj.core.api.Assertions.assertThat;

class AddressRepositoryTest extends IntegrationTestBase {

    @Autowired AddressRepository addressRepository;
    @Autowired StreetRepository streetRepository;
    @Autowired BldngRepository bldngRepository;

    @Test
    @Transactional
    void saveWithRelations() {
        Street street = streetRepository.save(new Street(null, "Зелёная"));
        Bldng bldng = bldngRepository.save(new Bldng(null, "316", null));

        Address saved = addressRepository.save(new Address(null, street, bldng));

        assertThat(saved.getId()).isNotNull();
        assertThat(addressRepository.findById(saved.getId()))
                .get()
                .satisfies(a -> {
                    assertThat(a.getStreet().getName()).isEqualTo("Зелёная");
                    assertThat(a.getBldng().getNumber()).isEqualTo("316");
                });
    }
}
