package ru.village.repository;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;
import ru.village.domain.Event;

import static org.assertj.core.api.Assertions.assertThat;

class EventRepositoryTest extends IntegrationTestBase {

    @Autowired EventRepository eventRepository;

    @Test
    void saveAndFind() {
        Event saved = eventRepository.save(new Event(null, "2026 март", new BigDecimal("500.00")));

        assertThat(saved.getId()).isNotNull();
        assertThat(eventRepository.findById(saved.getId()))
                .get()
                .extracting(Event::getCost)
                .isEqualTo(new BigDecimal("500.00"));
    }
}
