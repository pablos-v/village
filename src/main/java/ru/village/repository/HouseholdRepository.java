package ru.village.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.village.domain.Household;

public interface HouseholdRepository extends JpaRepository<Household, Long> {

    @Query("""
        SELECT h FROM Household h
        JOIN h.address a JOIN a.street s JOIN a.bldng b
        WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%'))
        ORDER BY s.name, b.number
        """)
    List<Household> searchByStreetSubstring(@Param("q") String q);
}
