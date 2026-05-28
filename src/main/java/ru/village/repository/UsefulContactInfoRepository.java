package ru.village.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.village.domain.UsefulContactInfo;

public interface UsefulContactInfoRepository extends JpaRepository<UsefulContactInfo, Long> {
}
