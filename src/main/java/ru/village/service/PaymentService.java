package ru.village.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.village.controller.dto.response.PaymentResponse;
import ru.village.mapper.PaymentMapper;
import ru.village.repository.PaymentRepository;

/** Чтение поступлений: последние N + фильтр по год/месяц с пагинацией. */
@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
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
}
