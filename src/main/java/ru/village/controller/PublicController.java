package ru.village.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Контроллер публичной (USER+) зоны. Заглушка для Эпика 2. */
@RestController
public class PublicController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
