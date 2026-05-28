package ru.village.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.village.domain.BalanceView;

public interface BalanceViewRepository extends JpaRepository<BalanceView, java.math.BigDecimal> {

    @Query(value = "SELECT amount FROM village.balance_view", nativeQuery = true)
    BalanceView findCurrent();
}
