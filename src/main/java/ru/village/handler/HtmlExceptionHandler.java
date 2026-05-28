package ru.village.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.village.controller.AdminController;
import ru.village.controller.LoginController;
import ru.village.controller.PublicController;
import ru.village.exception.EntityNotFoundException;
import ru.village.exception.InsufficientBalanceException;

/** Перехватывает исключения из HTML-контроллеров (@Controller) и отдаёт error.html. */
@ControllerAdvice(assignableTypes = {PublicController.class, AdminController.class, LoginController.class})
@Slf4j
public class HtmlExceptionHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    public String insufficient(InsufficientBalanceException e, Model model) {
        log.warn("Insufficient balance (html): {}", e.getMessage());
        model.addAttribute("error", e.getMessage());
        return "error";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String notFound(EntityNotFoundException e, Model model) {
        log.warn("Not found (html): {}", e.getMessage());
        model.addAttribute("error", e.getMessage());
        return "error";
    }
}
