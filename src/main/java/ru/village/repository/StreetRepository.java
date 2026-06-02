package ru.village.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.village.domain.Street;

public interface StreetRepository extends JpaRepository<Street, Long> {

    List<Street> findAllByOrderByNameAsc();

    Optional<Street> findByNameIgnoreCase(String name);
}
