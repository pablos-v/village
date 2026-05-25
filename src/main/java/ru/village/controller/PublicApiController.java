package ru.village.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.village.controller.dto.response.AddressDto;
import ru.village.controller.dto.response.ExpenseResponse;
import ru.village.controller.dto.response.HistoryPeriod;
import ru.village.controller.dto.response.PaymentResponse;
import ru.village.service.IBalanceService;
import ru.village.service.IExpenseService;
import ru.village.service.IHistoryService;
import ru.village.service.IHouseholdService;
import ru.village.service.IPaymentService;

/** REST API для USER+ — поставляет JSON для Thymeleaf-страниц и AI-чата. */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicApiController {

    private final IBalanceService balanceService;
    private final IPaymentService paymentService;
    private final IExpenseService expenseService;
    private final IHistoryService historyService;
    private final IHouseholdService householdService;

    @GetMapping("/balance")
    public Map<String, BigDecimal> balance() {
        return Map.of("balance", balanceService.currentBalance());
    }

    @GetMapping("/payments/recent")
    public List<PaymentResponse> recentPayments(@RequestParam(defaultValue = "10") int limit) {
        return paymentService.recent(limit);
    }

    @GetMapping("/payments")
    public Page<PaymentResponse> payments(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @PageableDefault(size = 50) Pageable pageable) {
        return paymentService.findAll(year, month, pageable);
    }

    @GetMapping("/expenses")
    public Page<ExpenseResponse> expenses(@PageableDefault(size = 50) Pageable pageable) {
        return expenseService.findAll(pageable);
    }

    @GetMapping("/history/periods")
    public List<HistoryPeriod> historyPeriods() {
        return historyService.periods();
    }

    @GetMapping("/addresses")
    public List<AddressDto> addresses(@RequestParam(required = false, defaultValue = "") String q) {
        return householdService.searchAddresses(q);
    }
}
