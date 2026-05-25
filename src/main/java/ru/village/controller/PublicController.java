package ru.village.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
}
