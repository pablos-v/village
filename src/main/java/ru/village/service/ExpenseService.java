package ru.village.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.village.controller.dto.request.CreateExpenseRequest;
import ru.village.controller.dto.response.ExpenseResponse;
import ru.village.domain.Expense;
import ru.village.exception.EntityNotFoundException;
import ru.village.exception.InsufficientBalanceException;
import ru.village.mapper.ExpenseMapper;
import ru.village.repository.EventRepository;
import ru.village.repository.ExpenseRepository;

/** Чтение расходов + создание новых (с проверкой остатка кассы). */
@Service
@RequiredArgsConstructor
public class ExpenseService implements IExpenseService {

    private final ExpenseRepository expenseRepository;
    private final EventRepository eventRepository;
    private final IBalanceService balanceService;
    private final ExpenseMapper expenseMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> findAll(Pageable pageable) {
        return expenseRepository.findAllOrdered(pageable).map(expenseMapper::toResponse);
    }

    @Override
    @Transactional
    public ExpenseResponse create(CreateExpenseRequest req) {
        var event = eventRepository.findById(req.eventId())
                .orElseThrow(() -> new EntityNotFoundException("Event " + req.eventId() + " не найден"));
        var currentBalance = balanceService.currentBalance();
        if (req.amount().compareTo(currentBalance) > 0) {
            throw new InsufficientBalanceException(
                    "Расход " + req.amount() + " больше остатка " + currentBalance);
        }
        Expense saved = expenseRepository.save(new Expense(
                null, event, req.amount(), req.date(), req.comment()));
        return expenseMapper.toResponse(saved);
    }
}
