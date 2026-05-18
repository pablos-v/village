# Архитектура Village

---

## Слоистая архитектура

```
┌─────────────────────────────────────────┐
│   PRESENTATION (Контроллеры + View)     │ ← Thymeleaf
├─────────────────────────────────────────┤
│   APPLICATION (Бизнес-логика)          │ ← Service layer
├─────────────────────────────────────────┤
│   DOMAIN (Доменные модели)             │ ← JPA entities
├─────────────────────────────────────────┤
│   INFRASTRUCTURE (Data Access)         │ ← Repositories
└─────────────────────────────────────────┘
```

DTO не выходят за пределы controller. Service работает с entities.

---

## Структура пакетов

```
ru.village/
├── controller/
│   ├── dto/
│   │   ├── request/                     # Входящие DTO
│   │   └── response/                    # Исходящие DTO
│   ├── AdminController                  # ADMIN + OPERATOR (формы, POST)
│   └── PublicController                 # Все пользователи (просмотр, GET)
├── service/                             # Интерфейс + Impl
│   ├── PaymentService                   # Платежи (CRUD, фильтры, последние)
│   ├── ExpenseService                   # Расходы (CRUD)
│   ├── BalanceService                   # Чтение из VIEW balance_view
│   ├── HouseholdService                 # Адреса для выпадающего списка
│   ├── EventService                     # События/периоды (CRUD)
│   ├── ContactInfoService               # Полезные контакты (CRUD)
│   ├── HistoryService                   # История по периодам
│   └── AiChatService                    # AI ассистент (text-to-SQL)
├── domain/                              # JPA entities
│   ├── Street
│   ├── Building
│   ├── Address
│   ├── Household                        # без master_inh_id
│   ├── Inhabitant                       # + is_master boolean
│   ├── Event
│   ├── Payment                          # + amount, + event FK
│   ├── Expense                          # + event_id FK
│   └── UsefulContactInfo
├── repository/                          # Spring Data JPA
├── mapper/                              # MapStruct @Mapper(componentModel = "spring")
├── exception/                           # Custom exceptions
├── handler/                             # GlobalExceptionHandler
└── config/                              # SecurityConfig, AiConfig
```

---

## Роли Spring Security

| Роль | Права | Endpoints |
|------|-------|-----------|
| ADMIN | Полный доступ | Все |
| OPERATOR | Внесение приходов/расходов, управление событиями | POST `/api/payments`, POST `/api/expenses`, POST `/api/events`, `/admin/**` |
| USER | Один общий аккаунт, только чтение (защита от индексации, не разграничение) | GET `/`, `/payments`, `/expenses`, `/history`, GET `/api/**` |

**Public (без логина):** статика (`/css/**`, `/js/**`), `/login`, `/actuator/health`

---

## API Endpoints

### Публичные (USER+)

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/` | Главная страница |
| GET | `/payments` | Все поступления с фильтрами |
| GET | `/expenses` | История расходов |
| GET | `/history` | История по периодам |
| GET | `/api/payments/recent?limit=10` | Последние 10 поступлений |
| GET | `/api/payments?page=0&size=50&year=2026&month=3` | Все платежи с пагинацией |
| GET | `/api/expenses?page=0&size=50` | Все расходы с пагинацией |
| GET | `/api/balance` | Текущий остаток (из VIEW `balance_view`) |
| GET | `/api/history/periods` | История по периодам |
| GET | `/api/contacts` | Полезная информация |
| GET | `/api/addresses?q=зелёная` | Адреса для выпадающего списка (поиск по подстроке) |
| GET | `/api/events` | Список всех событий |
| POST | `/api/ai/chat` | Вопрос к AI ассистенту |

### Оператор/Админ (OPERATOR+)

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/admin/payment/new` | Форма внесения прихода |
| GET | `/admin/expense/new` | Форма внесения расхода |
| GET | `/admin/events` | Управление событиями |
| POST | `/api/payments` | Сохранить приход |
| POST | `/api/expenses` | Сохранить расход |
| POST | `/api/events` | Создать новое событие |

### Примеры тел запросов

**POST `/api/payments`**
```json
{
  "eventId": 2,
  "householdId": 5,
  "amount": 500,
  "date": "2026-05-17"
}
```

**POST `/api/expenses`**
```json
{
  "eventId": 2,
  "amount": 5000,
  "comment": "Вывоз мусора",
  "date": "2026-05-17"
}
```

**POST `/api/events`**
```json
{
  "name": "2026 апрель",
  "cost": 500
}
```

**POST `/api/ai/chat`**
```json
{
  "message": "Какой дом сдал больше всех в этом году?"
}
```

---

## БД — итоговая схема

Все таблицы проекта создаются в схеме `village` (не `public`). Это обязательно для ограничения AI-ассистента — см. ниже.

### Базовые таблицы

| Таблица | Поля |
|---------|------|
| `street` | id, name |
| `bldng` | id, number, dscrptn |
| `address` | id, street_id FK, bldng_id FK |
| `household` | id, addrss_id FK |
| `inhabitant` | id, name, phone, hh_id FK, is_master boolean |
| `events` | id, name, cost |
| `payment` | id, hh_id FK, paydate, evnt_id FK→events.id, **amount numeric** |
| `expense` | id, event_id FK→events.id, amount numeric, date date, comment varchar |
| `useful_contact_info` | id, type varchar, contact_info text, comment varchar |

### Изменения относительно референсного dump'а ([DB.sql](DB.sql))

| Что | Как |
|-----|-----|
| `household.master_inh_id` | **удалено** — циклический FK с `inhabitant.hh_id` разорван |
| `inhabitant.is_master` | **добавлено** (boolean) — мастер-житель домохозяйства определяется по этому флагу |
| `payment.amount` | **добавлено** (numeric) — индивидуальная сумма платежа |
| `payment.evnt_id` | **+ FK на events.id** |

### VIEW

| Объект | Описание |
|--------|----------|
| `balance_view` | Обычный VIEW: `SELECT SUM(payment.amount) - SUM(expense.amount)`. Materialized View с триггерами **не используется** — для одного посёлка обычный VIEW отрабатывает за миллисекунды, а MV блокировал бы каждый INSERT/UPDATE/DELETE. |

### Формат адреса

Адрес в UI: `"ул. Зелёная, д. 316"` — собирается через JOIN:
```
street.name || ', д. ' || bldng.number
```

---

## AI Ассистент

Подробное описание: [INTERFACE.md](INTERFACE.md) → раздел "Text-to-SQL с валидацией"

**Схема работы:** Пользователь → Spring AI → генерация SQL → валидация (только SELECT + только схема `village`) → выполнение → форматирование ответа

**Уровни защиты:** отдельная роль БД с GRANT SELECT только на схему `village` (никакого `pg_catalog`/`information_schema`), `search_path = village`, statement_timeout, валидация SQL в Java, system prompt. Подробности — [README.md](README.md) → "AI Ассистент".

**Зависимости:** `org.springframework.ai:spring-ai-starter-model-openai` + BOM `org.springframework.ai:spring-ai-bom`. Для OpenRouter переопределяется `spring.ai.openai.base-url` в application.yaml.

---

## Деплой

- **Docker:** Dockerfile (eclipse-temurin:25-jre-alpine) + docker-compose.prod.yml (nginx-proxy + letsencrypt)
- **CI/CD:** GitHub Actions — build → deploy по push в main
- **Подробнее:** [README.md](README.md) → раздел "Docker deploy"
