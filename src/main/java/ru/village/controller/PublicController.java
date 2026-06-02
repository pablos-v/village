package ru.village.controller;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.Comparator;
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

    /** Номер месяца → русское название, по порядку. */
    private static final Map<Integer, String> MONTHS = new LinkedHashMap<>();
    static {
        String[] names = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        for (int i = 0; i < 12; i++) MONTHS.put(i + 1, names[i]);
    }

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
            @PageableDefault(size = 20) Pageable pageable,
            Model model
    ) {
        // фильтр по умолчанию выключен: year/month == null → показываем все, новые сверху
        // годы с платежами + всегда текущий, по убыванию
        TreeSet<Integer> years = new TreeSet<>(Comparator.reverseOrder());
        years.addAll(paymentService.availableYears());
        years.add(LocalDate.now().getYear());

        model.addAttribute("payments", paymentService.findAll(year, month, pageable));
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("years", years);
        model.addAttribute("months", MONTHS);
        return "payments";
    }

    @GetMapping("/expenses")
    public String expenses(@PageableDefault(size = 20) Pageable pageable, Model model) {
        model.addAttribute("expenses", expenseService.findAll(pageable));
        return "expenses";
    }

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("periods", historyService.periods());
        return "history";
    }
}
