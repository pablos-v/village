package ru.village.service;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.village.controller.dto.response.HistoryPeriod;
import ru.village.repository.EventRepository;
import ru.village.repository.ExpenseRepository;
import ru.village.repository.PaymentRepository;

/** Агрегаты по событиям: сколько собрано и сколько потрачено за каждый event. */
@Service
@RequiredArgsConstructor
public class HistoryService implements IHistoryService {

    private final EventRepository eventRepository;
    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HistoryPeriod> periods() {
        return eventRepository.findAll().stream()
                .map(ev -> new HistoryPeriod(
                        ev.getId(),
                        ev.getName(),
                        sumOrZero(paymentRepository.sumByEvent(ev.getId())),
                        sumOrZero(expenseRepository.sumByEvent(ev.getId()))))
                .toList();
    }

    private BigDecimal sumOrZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
