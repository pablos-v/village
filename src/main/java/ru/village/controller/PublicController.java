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
import ru.village.service.IHistoryService;
import ru.village.service.IPaymentService;
import ru.village.service.IUsefulContactInfoService;

/** Публичные страницы для USER+. */
@Controller
@RequiredArgsConstructor
public class PublicController {

    private final IBalanceService balanceService;
    private final IPaymentService paymentService;
    private final IExpenseService expenseService;
    private final IHistoryService historyService;
    private final IUsefulContactInfoService contactService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("balance", balanceService.currentBalance());
        model.addAttribute("recentPayments", paymentService.recent(10));
        model.addAttribute("recentExpenses", expenseService.findAll(PageRequest.of(0, 5)).getContent());
        model.addAttribute("contacts", contactService.findAll());
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

    @GetMapping("/expenses")
    public String expenses(@PageableDefault(size = 50) Pageable pageable, Model model) {
        model.addAttribute("expenses", expenseService.findAll(pageable));
        return "expenses";
    }

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("periods", historyService.periods());
        return "history";
    }
}
