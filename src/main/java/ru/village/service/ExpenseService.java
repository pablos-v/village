package ru.village.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.village.controller.dto.request.CreateExpenseRequest;
import ru.village.controller.dto.response.ExpenseEditDto;
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
@Slf4j
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
        log.info("audit: expense created id={} event={} amount={} comment='{}' by user={}",
                saved.getId(), req.eventId(), req.amount(), req.comment(), currentUser());
        return expenseMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseEditDto getForEdit(Long id) {
        var e = expenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Expense " + id + " не найден"));
        return new ExpenseEditDto(e.getId(), e.getEvent().getId(), e.getAmount(), e.getDate(), e.getComment());
    }

    @Override
    @Transactional
    public ExpenseResponse update(Long id, CreateExpenseRequest req) {
        var expense = expenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Expense " + id + " не найден"));
        var event = eventRepository.findById(req.eventId())
                .orElseThrow(() -> new EntityNotFoundException("Event " + req.eventId() + " не найден"));
        // доступно = текущий остаток + старая сумма этого расхода (она уже вычтена из остатка)
        var available = balanceService.currentBalance().add(expense.getAmount());
        if (req.amount().compareTo(available) > 0) {
            throw new InsufficientBalanceException(
                    "Расход " + req.amount() + " больше доступного остатка " + available);
        }
        expense.setEvent(event);
        expense.setAmount(req.amount());
        expense.setDate(req.date());
        expense.setComment(req.comment());
        Expense saved = expenseRepository.save(expense);
        log.info("audit: expense updated id={} event={} amount={} comment='{}' by user={}",
                saved.getId(), req.eventId(), req.amount(), req.comment(), currentUser());
        return expenseMapper.toResponse(saved);
    }

    private static String currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "system" : auth.getName();
    }
}
