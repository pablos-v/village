package ru.village.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.village.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p ORDER BY p.paydate DESC, p.id DESC")
    List<Payment> findRecent(Pageable pageable);

    @Query("""
        SELECT p FROM Payment p
        WHERE (:year IS NULL OR EXTRACT(YEAR FROM p.paydate) = :year)
          AND (:month IS NULL OR EXTRACT(MONTH FROM p.paydate) = :month)
        ORDER BY p.paydate DESC, p.id DESC
        """)
    Page<Payment> findFiltered(@Param("year") Integer year, @Param("month") Integer month, Pageable pageable);
}
