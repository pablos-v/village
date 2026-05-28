package ru.village.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;
import ru.village.domain.Street;

import static org.assertj.core.api.Assertions.assertThat;

class StreetRepositoryTest extends IntegrationTestBase {

    @Autowired
    StreetRepository streetRepository;

    @Test
    void saveAndFind() {
        Street saved = streetRepository.save(new Street(null, "Зелёная"));

        assertThat(saved.getId()).isNotNull();
        assertThat(streetRepository.findById(saved.getId()))
                .get()
                .extracting(Street::getName)
                .isEqualTo("Зелёная");
    }
}
