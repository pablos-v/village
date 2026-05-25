package ru.village.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Контроллер административной зоны (OPERATOR+). Заглушка для Эпика 2. */
@RestController
public class AdminController {

    @GetMapping("/admin/ping")
    public String adminPing() {
        return "admin pong";
    }
}
