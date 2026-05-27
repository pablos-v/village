package ru.village.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Добавляет общие атрибуты во все HTML-страницы. */
@ControllerAdvice
public class GlobalModelAttributes {

    /** Текущий путь запроса — для подсветки активной кнопки навигации. */
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
