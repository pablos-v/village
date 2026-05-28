package ru.village.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.village.domain.Street;

public interface StreetRepository extends JpaRepository<Street, Long> {
}
