package ru.village.service;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.IntegrationTestBase;
import ru.village.controller.dto.request.CreateEventRequest;

import static org.assertj.core.api.Assertions.assertThat;

class EventServiceTest extends IntegrationTestBase {

    @Autowired IEventService eventService;

    @Test
    void createEvent() {
        var event = eventService.create(new CreateEventRequest("2026 апрель", new BigDecimal("500")));
        assertThat(event.id()).isNotNull();
        assertThat(event.name()).isEqualTo("2026 апрель");
    }

    @Test
    void listEvents() {
        eventService.create(new CreateEventRequest("a", new BigDecimal("100")));
        eventService.create(new CreateEventRequest("b", new BigDecimal("200")));
        assertThat(eventService.findAll()).hasSizeGreaterThanOrEqualTo(2);
    }
}
