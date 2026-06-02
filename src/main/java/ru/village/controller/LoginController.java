package ru.village.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Единая витрина входа: одно поле пароля, роль (USER/OPERATOR/ADMIN) определяется по паролю. */
@Controller
public class LoginController {

    /** Витрина: одно поле «пароль доступа». */
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
}
