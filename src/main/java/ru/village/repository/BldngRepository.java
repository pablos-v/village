package ru.village.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.village.domain.Bldng;

public interface BldngRepository extends JpaRepository<Bldng, Long> {
}
