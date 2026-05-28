package ru.village.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;
import ru.village.controller.dto.request.SaveContactRequest;
import ru.village.exception.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsefulContactInfoServiceTest extends IntegrationTestBase {

    @Autowired IUsefulContactInfoService service;

    @Test
    void createAndList() {
        service.create(new SaveContactRequest("терапевт", "Иванов И.И. +7 (999) 123-45-67", "каб. 312"));
        service.create(new SaveContactRequest("педиатр", "Петрова П.П. +7 (999) 765-43-21", null));

        var all = service.findAll();
        assertThat(all).hasSize(2);
        assertThat(all).extracting("type").contains("терапевт", "педиатр");
    }

    @Test
    void update() {
        var created = service.create(new SaveContactRequest("терапевт", "old", null));
        var updated = service.update(created.id(), new SaveContactRequest("терапевт", "new", "обновлено"));
        assertThat(updated.contactInfo()).isEqualTo("new");
        assertThat(updated.comment()).isEqualTo("обновлено");
    }

    @Test
    void delete() {
        var created = service.create(new SaveContactRequest("x", "y", null));
        service.delete(created.id());
        assertThat(service.findAll()).isEmpty();
    }

    @Test
    void updateMissingThrows() {
        assertThatThrownBy(() -> service.update(999_999L, new SaveContactRequest("a", "b", null)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteMissingThrows() {
        assertThatThrownBy(() -> service.delete(999_999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
