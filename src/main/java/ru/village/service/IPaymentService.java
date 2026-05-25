package ru.village.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.village.controller.dto.request.CreatePaymentRequest;
import ru.village.controller.dto.response.PaymentResponse;

public interface IPaymentService {
    List<PaymentResponse> recent(int limit);
    Page<PaymentResponse> findAll(Integer year, Integer month, Pageable pageable);
    PaymentResponse create(CreatePaymentRequest req);
}
