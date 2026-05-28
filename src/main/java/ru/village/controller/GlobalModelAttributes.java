package ru.village.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.village.config.AppBrandingProperties;

/** Добавляет общие атрибуты во все HTML-страницы. */
@ControllerAdvice
@RequiredArgsConstructor
@EnableConfigurationProperties(AppBrandingProperties.class)
public class GlobalModelAttributes {

    private final AppBrandingProperties branding;

    /** Текущий путь запроса — для подсветки активной кнопки навигации. */
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /** Краткое имя продукта (navbar, заголовок витрины). */
    @ModelAttribute("brand")
    public String brand() {
        return branding.brand();
    }

    /** Слоган в футере. */
    @ModelAttribute("tagline")
    public String tagline() {
        return branding.tagline();
    }
}
