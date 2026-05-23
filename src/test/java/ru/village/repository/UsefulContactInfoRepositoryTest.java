package ru.village.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;
import ru.village.domain.UsefulContactInfo;

import static org.assertj.core.api.Assertions.assertThat;

class UsefulContactInfoRepositoryTest extends IntegrationTestBase {

    @Autowired UsefulContactInfoRepository repository;

    @Test
    void saveAndFind() {
        UsefulContactInfo saved = repository.save(new UsefulContactInfo(
                null, "терапевт", "Иванов И.И. +7 (999) 123-45-67", "каб. 312"));

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findAll()).hasSize(1);
    }
}
