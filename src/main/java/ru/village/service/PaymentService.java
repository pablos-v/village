package ru.village.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.village.controller.dto.request.CreatePaymentRequest;
import ru.village.controller.dto.response.PaymentResponse;
import ru.village.domain.Payment;
import ru.village.exception.EntityNotFoundException;
import ru.village.mapper.PaymentMapper;
import ru.village.repository.EventRepository;
import ru.village.repository.HouseholdRepository;
import ru.village.repository.PaymentRepository;

/** Чтение поступлений + создание новых записей. */
@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final EventRepository eventRepository;
    private final HouseholdRepository householdRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> recent(int limit) {
        return paymentRepository.findRecent(PageRequest.of(0, limit))
                .stream().map(paymentMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAll(Integer year, Integer month, Pageable pageable) {
        return paymentRepository.findFiltered(year, month, pageable).map(paymentMapper::toResponse);
    }

    @Override
    @Transactional
    public PaymentResponse create(CreatePaymentRequest req) {
        var event = eventRepository.findById(req.eventId())
                .orElseThrow(() -> new EntityNotFoundException("Event " + req.eventId() + " не найден"));
        var household = householdRepository.findById(req.householdId())
                .orElseThrow(() -> new EntityNotFoundException("Household " + req.householdId() + " не найден"));

        Payment saved = paymentRepository.save(new Payment(null, household, req.date(), event, req.amount()));
        return paymentMapper.toResponse(saved);
    }
}
