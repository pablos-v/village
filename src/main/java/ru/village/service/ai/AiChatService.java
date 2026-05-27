package ru.village.service.ai;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/** Чат-ассистент со статистикой сборов. LLM генерирует SQL → SqlSecurityValidator → village_ai JDBC. */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatService {

    private final ChatClient chatClient;
    private final SqlSecurityValidator validator;

    @Qualifier("aiJdbcTemplate")
    private final JdbcTemplate aiJdbcTemplate;

    private static final String SYSTEM_PROMPT = """
            Ты — ассистент по статистике денежных сборов на улице (СНТ).
            Когда нужно посчитать данные — вызови инструмент executeSql с SQL-запросом, потом сформулируй ответ.

            ПРАВИЛА:
            1. Генерируй ТОЛЬКО SELECT-запросы.
            2. Используй ТОЛЬКО таблицы схемы village (без префикса schema — search_path уже выставлен):
               street, bldng, address, household, inhabitant, events, payment, expense, balance_view.
            3. НЕ используй pg_catalog, information_schema.
            4. НЕ используй FOR UPDATE, FOR SHARE, LOCK.
            5. Формулируй ответы на русском, дружелюбно, без технических деталей.
            6. На вопросы вне темы отвечай:
               "Извините, я могу отвечать только на вопросы по статистике денежных сборов."

            СТРУКТУРА БД:
            - street (id, name)
            - bldng (id, number, dscrptn)
            - address (id, street_id, bldng_id)
            - household (id, addrss_id)
            - inhabitant (id, name, phone, hh_id, is_master)
            - events (id, name, cost)
            - payment (id, hh_id, paydate, evnt_id, amount)
            - expense (id, event_id, amount, date, comment)
            - balance_view (amount) — текущий остаток (SUM payments − SUM expenses)
            """;

    @Tool(description = "Выполнить SELECT-запрос к схеме village и вернуть строки. " +
            "Запрещены DELETE/INSERT/UPDATE и любые DDL — будут заблокированы валидатором.")
    public List<Map<String, Object>> executeSql(
            @ToolParam(description = "SQL SELECT-запрос по таблицам схемы village") String sql) {
        String safe = validator.validate(sql);
        log.info("AI SQL: {}", safe);
        return aiJdbcTemplate.queryForList(safe);
    }

    public String chat(String userMessage) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)
                .tools(this)
                .call()
                .content();
    }
}
