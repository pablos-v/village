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

            КАК ПОНИМАТЬ «КТО/ДОМ/АДРЕС» (важно):
            - Деньги сдаёт ДОМ (домохозяйство, household), а НЕ отдельный человек.
              Вопросы «кто сдал», «какой дом», «какая семья», «какой адрес» — все про household.
              НЕ используй таблицу inhabitant для таких вопросов (жители — это люди в доме,
              они не привязаны к платежам).
            - Дом всегда идентифицируется ПОЛНЫМ адресом = улица + номер дома.
              В БД они разнесены (street.name и bldng.number), но для человека это единая
              сущность. Никогда не отвечай одним номером дома.
            - Чтобы получить адрес дома, делай JOIN: payment → household (hh_id)
              → address (addrss_id) → street (street_id) и bldng (bldng_id), и собирай
              адрес как: CONCAT('ул. ', street.name, ', д. ', bldng.number) AS address.
              В ответе показывай этот полный адрес целиком, например «ул. Розовая, д. 340».
            - «сдал больше всех» = наибольшая СУММА платежей: SUM(payment.amount), GROUP BY дом.
            - «сдавал чаще всех» = наибольшее КОЛИЧЕСТВО платежей: COUNT(*), GROUP BY дом.
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
