package ru.village.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.village.service.IBalanceService;
import ru.village.service.IExpenseService;
import ru.village.service.IPaymentService;

/** Публичные страницы для USER+. */
@Controller
@RequiredArgsConstructor
public class PublicController {

    private final IBalanceService balanceService;
    private final IPaymentService paymentService;
    private final IExpenseService expenseService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("balance", balanceService.currentBalance());
        model.addAttribute("recentPayments", paymentService.recent(10));
        model.addAttribute("recentExpenses", expenseService.findAll(PageRequest.of(0, 5)).getContent());
        return "index";
    }

    @GetMapping("/payments")
    public String payments(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @PageableDefault(size = 50) Pageable pageable,
            Model model
    ) {
        model.addAttribute("payments", paymentService.findAll(year, month, pageable));
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        return "payments";
    }
}
