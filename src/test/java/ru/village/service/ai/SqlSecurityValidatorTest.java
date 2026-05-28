package ru.village.service.ai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SqlSecurityValidatorTest {

    SqlSecurityValidator validator = new SqlSecurityValidator();

    @Test
    void allowsSimpleSelect() {
        assertThatCode(() -> validator.validate("SELECT SUM(amount) FROM payment"))
                .doesNotThrowAnyException();
    }

    @Test
    void stripsTrailingSemicolon() {
        // LLM часто добавляет ; в конце — обрезаем, не блокируем
        assertThat(validator.validate("SELECT COUNT(*) FROM payment;"))
                .isEqualTo("SELECT COUNT(*) FROM payment");
    }

    @Test
    void midStatementSemicolonStillRejected() {
        // обрезается только хвостовой ; — инъекция в середине остаётся запрещённой
        assertThatThrownBy(() -> validator.validate("SELECT 1; DROP TABLE x;"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void rejectsDelete() {
        assertThatThrownBy(() -> validator.validate("DELETE FROM payment"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void rejectsNotStartingWithSelect() {
        assertThatThrownBy(() -> validator.validate("WITH x AS (SELECT 1) SELECT * FROM x"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void rejectsSemicolon() {
        assertThatThrownBy(() -> validator.validate("SELECT 1; DROP TABLE x"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void rejectsCommentDashes() {
        assertThatThrownBy(() -> validator.validate("SELECT 1 -- DROP"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void rejectsBlockComment() {
        assertThatThrownBy(() -> validator.validate("SELECT 1 /* x */"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void rejectsPgCatalog() {
        assertThatThrownBy(() -> validator.validate("SELECT * FROM pg_catalog.pg_user"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void rejectsInformationSchema() {
        assertThatThrownBy(() -> validator.validate("SELECT * FROM information_schema.tables"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void rejectsPgSleep() {
        assertThatThrownBy(() -> validator.validate("SELECT pg_sleep(60)"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void rejectsForUpdate() {
        assertThatThrownBy(() -> validator.validate("SELECT * FROM payment FOR UPDATE"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void caseInsensitive() {
        assertThatThrownBy(() -> validator.validate("SeLeCt * FrOm PaYmEnT; DrOp TaBlE x"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void rejectsBlank() {
        assertThatThrownBy(() -> validator.validate("   "))
                .isInstanceOf(SecurityException.class);
    }
}
