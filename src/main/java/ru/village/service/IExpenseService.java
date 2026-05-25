package ru.village.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.village.controller.dto.request.CreateExpenseRequest;
import ru.village.controller.dto.response.ExpenseResponse;

public interface IExpenseService {
    Page<ExpenseResponse> findAll(Pageable pageable);
    ExpenseResponse create(CreateExpenseRequest req);
}
