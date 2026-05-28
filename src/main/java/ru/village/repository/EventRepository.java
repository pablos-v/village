package ru.village.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.village.domain.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
