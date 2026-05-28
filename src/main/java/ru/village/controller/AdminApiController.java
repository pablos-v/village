package ru.village.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.village.controller.dto.request.CreateEventRequest;
import ru.village.controller.dto.request.CreateExpenseRequest;
import ru.village.controller.dto.request.CreatePaymentRequest;
import ru.village.controller.dto.response.EventResponse;
import ru.village.controller.dto.response.ExpenseResponse;
import ru.village.controller.dto.response.PaymentResponse;
import ru.village.service.IEventService;
import ru.village.service.IExpenseService;
import ru.village.service.IPaymentService;

/** REST API для OPERATOR+: POST для создания событий, поступлений, расходов. */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminApiController {

    private final IPaymentService paymentService;
    private final IExpenseService expenseService;
    private final IEventService eventService;

    @PostMapping("/events")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(req));
    }

    @PostMapping("/payments")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(req));
    }

    @PostMapping("/expenses")
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody CreateExpenseRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.create(req));
    }
}
