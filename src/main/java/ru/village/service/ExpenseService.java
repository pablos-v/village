package ru.village.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.village.controller.dto.response.ExpenseResponse;
import ru.village.mapper.ExpenseMapper;
import ru.village.repository.ExpenseRepository;

/** Чтение расходов с пагинацией, упорядочены по дате DESC. */
@Service
@RequiredArgsConstructor
public class ExpenseService implements IExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> findAll(Pageable pageable) {
        return expenseRepository.findAllOrdered(pageable).map(expenseMapper::toResponse);
    }
}
