package ru.village.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.village.domain.Inhabitant;

public interface InhabitantRepository extends JpaRepository<Inhabitant, Long> {
    List<Inhabitant> findByHouseholdId(Long householdId);
}
