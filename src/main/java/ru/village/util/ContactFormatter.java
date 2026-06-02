package ru.village.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

/** Превращает номера вида +7… внутри произвольного текста в кликабельные tel:-ссылки. */
@Component("contactFormatter")
public class ContactFormatter {

    /** +7, далее цифры/пробелы/скобки/дефисы, заканчивается цифрой (минимум ~7 знаков). */
    private static final Pattern PHONE = Pattern.compile("\\+7[\\d\\s()\\-]{6,}\\d");

    /**
     * Возвращает HTML: текст экранируется, найденные номера +7… оборачиваются в
     * {@code <a href="tel:+7...">}. Остальной текст сохраняется как есть.
     */
    public String phoneLinks(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Matcher m = PHONE.matcher(text);
        int last = 0;
        while (m.find()) {
            sb.append(HtmlUtils.htmlEscape(text.substring(last, m.start())));
            String phone = m.group();
            String href = "tel:" + phone.replaceAll("[^+0-9]", "");
            sb.append("<a href=\"").append(href).append("\">")
                    .append(HtmlUtils.htmlEscape(phone)).append("</a>");
            last = m.end();
        }
        sb.append(HtmlUtils.htmlEscape(text.substring(last)));
        return sb.toString();
    }
}
