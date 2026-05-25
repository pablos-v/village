package ru.village.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.village.controller.dto.request.CreateEventRequest;
import ru.village.service.IEventService;

/** HTML-формы операторской зоны (OPERATOR+). */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IEventService eventService;

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
}
