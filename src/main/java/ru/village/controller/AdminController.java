package ru.village.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.village.controller.dto.request.CreateEventRequest;
import ru.village.controller.dto.request.CreatePaymentRequest;
import ru.village.service.IEventService;
import ru.village.service.IPaymentService;

/** HTML-формы операторской зоны (OPERATOR+). */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IEventService eventService;
    private final IPaymentService paymentService;

    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("events", eventService.findAll());
        return "admin/events";
    }

    @GetMapping("/events/new")
    public String newEventForm(Model model) {
        model.addAttribute("form", new CreateEventRequest("", null));
        return "admin/event-new";
    }

    @PostMapping("/events")
    public String createEvent(@Valid @ModelAttribute("form") CreateEventRequest form, BindingResult br) {
        if (br.hasErrors()) return "admin/event-new";
        eventService.create(form);
        return "redirect:/admin/events";
    }

    @GetMapping("/payment/new")
    public String newPaymentForm(Model model) {
        model.addAttribute("form", new CreatePaymentRequest(null, null, null, LocalDate.now()));
        model.addAttribute("events", eventService.findAll());
        return "admin/payment-new";
    }

    @PostMapping("/payment")
    public String createPayment(
            @Valid @ModelAttribute("form") CreatePaymentRequest form,
            BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("events", eventService.findAll());
            return "admin/payment-new";
        }
        paymentService.create(form);
        return "redirect:/";
    }
}
