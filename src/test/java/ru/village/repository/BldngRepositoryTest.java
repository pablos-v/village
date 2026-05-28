package ru.village.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;
import ru.village.domain.Bldng;

import static org.assertj.core.api.Assertions.assertThat;

class BldngRepositoryTest extends IntegrationTestBase {

    @Autowired
    BldngRepository bldngRepository;

    @Test
    void saveAndFind() {
        Bldng saved = bldngRepository.save(new Bldng(null, "316", null));

        assertThat(saved.getId()).isNotNull();
        assertThat(bldngRepository.findById(saved.getId()))
                .get()
                .extracting(Bldng::getNumber)
                .isEqualTo("316");
    }
}
