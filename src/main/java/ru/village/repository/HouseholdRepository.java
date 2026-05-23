package ru.village.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.village.domain.Household;

public interface HouseholdRepository extends JpaRepository<Household, Long> {
}
