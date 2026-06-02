package ru.village.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContactFormatterTest {

    private final ContactFormatter formatter = new ContactFormatter();

    @Test
    void wrapsPhoneInTheMiddleAndKeepsText() {
        String html = formatter.phoneLinks("Василиса Пупуловна +77788845579");
        assertThat(html).contains("Василиса Пупуловна ");
        assertThat(html).contains("<a href=\"tel:+77788845579\">+77788845579</a>");
    }

    @Test
    void wrapsBarePhone() {
        assertThat(formatter.phoneLinks("+77788845579"))
                .isEqualTo("<a href=\"tel:+77788845579\">+77788845579</a>");
    }

    @Test
    void stripsSeparatorsInHref() {
        String html = formatter.phoneLinks("+7 (778) 884-55-79");
        assertThat(html).contains("href=\"tel:+77788845579\"");
    }

    @Test
    void plainTextWithoutPhoneIsEscaped() {
        assertThat(formatter.phoneLinks("просто <текст>"))
                .isEqualTo("просто &lt;текст&gt;");
    }

    @Test
    void nullAndEmptyAreSafe() {
        assertThat(formatter.phoneLinks(null)).isEmpty();
        assertThat(formatter.phoneLinks("")).isEmpty();
    }
}
