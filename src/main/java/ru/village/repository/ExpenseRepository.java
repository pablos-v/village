package ru.village.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.village.domain.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e ORDER BY e.date DESC, e.id DESC")
    Page<Expense> findAllOrdered(Pageable pageable);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.event.id = :eventId")
    java.math.BigDecimal sumByEvent(@Param("eventId") Long eventId);
}
