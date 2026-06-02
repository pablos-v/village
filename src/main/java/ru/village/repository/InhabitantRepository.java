package ru.village.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.village.domain.Inhabitant;

public interface InhabitantRepository extends JpaRepository<Inhabitant, Long> {
    List<Inhabitant> findByHouseholdId(Long householdId);

    /** Жители дома: контактный (master) первым, затем по имени. */
    List<Inhabitant> findByHouseholdIdOrderByMasterDescNameAsc(Long householdId);

    void deleteByHouseholdId(Long householdId);

    /** Пары [householdId, count] — для подсчёта жителей в списке адресов. */
    @Query("SELECT i.household.id, COUNT(i) FROM Inhabitant i GROUP BY i.household.id")
    List<Object[]> countByHousehold();
}
