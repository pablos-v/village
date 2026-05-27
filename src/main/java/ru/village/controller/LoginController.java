package ru.village.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Страницы входа: витрина для жителей (/welcome) и форма для оператора/админа (/login-operator). */
@Controller
public class LoginController {

    /** Витрина: одно поле «пароль доступа», вход как USER. */
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    /** Полная форма логин/пароль для OPERATOR и ADMIN. */
    @GetMapping("/login-operator")
    public String loginOperator() {
        return "login-operator";
    }
}
