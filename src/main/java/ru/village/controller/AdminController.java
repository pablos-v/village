package ru.village.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.village.controller.dto.request.CreateEventRequest;
import ru.village.controller.dto.request.CreateExpenseRequest;
import ru.village.controller.dto.request.CreatePaymentRequest;
import ru.village.controller.dto.request.SaveContactRequest;
import ru.village.exception.EntityNotFoundException;
import ru.village.service.IEventService;
import ru.village.service.IExpenseService;
import ru.village.service.IPaymentService;
import ru.village.service.IUsefulContactInfoService;

/** HTML-формы операторской зоны (OPERATOR+). */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IEventService eventService;
    private final IPaymentService paymentService;
    private final IExpenseService expenseService;
    private final IUsefulContactInfoService contactService;

    /** Событие, предвыбираемое в формах прихода/расхода по умолчанию. */
    private static final String DEFAULT_EVENT_NAME = "На общие нужды";

    /** id события по умолчанию (или null, если его нет). */
    private Long defaultEventId() {
        return eventService.findAll().stream()
                .filter(e -> DEFAULT_EVENT_NAME.equals(e.name()))
                .map(ru.village.controller.dto.response.EventResponse::id)
                .findFirst()
                .orElse(null);
    }

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
        model.addAttribute("form", new CreatePaymentRequest(defaultEventId(), null, null, LocalDate.now()));
        model.addAttribute("events", eventService.findAll());
        model.addAttribute("formAction", "/admin/payment");
        model.addAttribute("formTitle", "Внести приход");
        model.addAttribute("addressLabel", "");
        return "admin/payment-new";
    }

    @PostMapping("/payment")
    public String createPayment(
            @Valid @ModelAttribute("form") CreatePaymentRequest form,
            BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("events", eventService.findAll());
            model.addAttribute("formAction", "/admin/payment");
            model.addAttribute("formTitle", "Внести приход");
            return "admin/payment-new";
        }
        paymentService.create(form);
        return "redirect:/payments";
    }

    @GetMapping("/payment/{id}/edit")
    public String editPaymentForm(@PathVariable Long id, Model model) {
        var p = paymentService.getForEdit(id);
        model.addAttribute("form", new CreatePaymentRequest(p.eventId(), p.householdId(), p.amount(), p.date()));
        model.addAttribute("events", eventService.findAll());
        model.addAttribute("formAction", "/admin/payment/" + id);
        model.addAttribute("formTitle", "Изменить приход");
        model.addAttribute("addressLabel", p.address());
        return "admin/payment-new";
    }

    @PostMapping("/payment/{id}")
    public String updatePayment(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") CreatePaymentRequest form,
            BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("events", eventService.findAll());
            model.addAttribute("formAction", "/admin/payment/" + id);
            model.addAttribute("formTitle", "Изменить приход");
            return "admin/payment-new";
        }
        paymentService.update(id, form);
        return "redirect:/payments";
    }

    @GetMapping("/expense/new")
    public String newExpenseForm(Model model) {
        model.addAttribute("form", new CreateExpenseRequest(defaultEventId(), null, LocalDate.now(), ""));
        model.addAttribute("events", eventService.findAll());
        model.addAttribute("formAction", "/admin/expense");
        model.addAttribute("formTitle", "Внести расход");
        return "admin/expense-new";
    }

    @PostMapping("/expense")
    public String createExpense(
            @Valid @ModelAttribute("form") CreateExpenseRequest form,
            BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("events", eventService.findAll());
            model.addAttribute("formAction", "/admin/expense");
            model.addAttribute("formTitle", "Внести расход");
            return "admin/expense-new";
        }
        expenseService.create(form);
        return "redirect:/expenses";
    }

    @GetMapping("/expense/{id}/edit")
    public String editExpenseForm(@PathVariable Long id, Model model) {
        var e = expenseService.getForEdit(id);
        model.addAttribute("form", new CreateExpenseRequest(e.eventId(), e.amount(), e.date(), e.comment()));
        model.addAttribute("events", eventService.findAll());
        model.addAttribute("formAction", "/admin/expense/" + id);
        model.addAttribute("formTitle", "Изменить расход");
        return "admin/expense-new";
    }

    @PostMapping("/expense/{id}")
    public String updateExpense(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") CreateExpenseRequest form,
            BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("events", eventService.findAll());
            model.addAttribute("formAction", "/admin/expense/" + id);
            model.addAttribute("formTitle", "Изменить расход");
            return "admin/expense-new";
        }
        expenseService.update(id, form);
        return "redirect:/expenses";
    }

    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("contacts", contactService.findAll());
        return "admin/contacts";
    }

    @GetMapping("/contacts/new")
    public String newContactForm(Model model) {
        model.addAttribute("form", new SaveContactRequest("", "", null));
        model.addAttribute("action", "/admin/contacts");
        return "admin/contact-form";
    }

    @PostMapping("/contacts")
    public String createContact(@Valid @ModelAttribute("form") SaveContactRequest form,
                                BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("action", "/admin/contacts");
            return "admin/contact-form";
        }
        contactService.create(form);
        return "redirect:/admin/contacts";
    }

    @GetMapping("/contacts/{id}/edit")
    public String editContactForm(@PathVariable Long id, Model model) {
        var c = contactService.findAll().stream().filter(x -> x.id().equals(id)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Contact " + id + " не найден"));
        model.addAttribute("form", new SaveContactRequest(c.type(), c.contactInfo(), c.comment()));
        model.addAttribute("action", "/admin/contacts/" + id);
        return "admin/contact-form";
    }

    @PostMapping("/contacts/{id}")
    public String updateContact(@PathVariable Long id,
                                @Valid @ModelAttribute("form") SaveContactRequest form,
                                BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("action", "/admin/contacts/" + id);
            return "admin/contact-form";
        }
        contactService.update(id, form);
        return "redirect:/admin/contacts";
    }

    @PostMapping("/contacts/{id}/delete")
    public String deleteContact(@PathVariable Long id) {
        contactService.delete(id);
        return "redirect:/admin/contacts";
    }
}
