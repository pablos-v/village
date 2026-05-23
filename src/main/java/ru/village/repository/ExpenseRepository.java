package ru.village.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.village.domain.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
