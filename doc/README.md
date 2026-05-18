# Village — Система учёта общественных сборов

## Описание проекта

Веб-приложение для учёта денежных сборов на улице (СНТ, коттеджный посёлок).

**Цель:** Заменить "тетрадку" и "доску объявлений" цифровым сервисом, где жители видят состояние сборов, а администратор легко вносит данные.

**Ключевая особенность:** Без реальных финансовых операций — только отчётность. Деньги остаются у администратора (бабушки), система лишь фиксирует поступления и расходы.

**Репозиторий:** https://github.com/pablos-v/village.git (приватный)

---

## Технологический стек

| Компонент | Технология | Версия |
|-----------|------------|--------|
| **Backend** | Spring Boot | 3.5.x (GA) |
| **Java** | | 25 |
| **Database** | PostgreSQL | 16+ |
| **Миграции** | Liquibase | |
| **ORM** | Spring Data JPA | |
| **Frontend** | Thymeleaf | |
| **Security** | Spring Security | |
| **Mapping** | MapStruct | 1.6+ |
| **AI** | Spring AI | 1.1.x (GA) |
| **AI Provider** | OpenRouter | Бесплатная модель |
| **Utils** | Lombok | |
| **Build** | Gradle | Kotlin DSL |
| **Docker** | + docker-compose | |
| **CI/CD** | GitHub Actions | |
| **HTTPS** | nginx-proxy + letsencrypt | Автосертификаты |

**Почему именно эта связка:** Spring AI 1.1.x официально поддерживает только Spring Boot 3.5.x. Spring Boot 4.x требует Spring AI 2.x, который пока в milestone. Берём всё GA для стабильности — переход на 4.x/2.x можно сделать позже одним апгрейдом.

### Dependency Management через BOM

Версии зависимостей управляются через BOM (Bill of Materials):

```kotlin
dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}
```

Артефакт Spring AI starter (актуальный для Spring AI 1.0+): `org.springframework.ai:spring-ai-starter-model-openai` (без указания версии — версия берётся из BOM).

---

## Сущности базы данных

### Существующие таблицы

| Таблица | Описание |
|---------|----------|
| `street` | Улицы (id, name) |
| `bldng` | Здания/номера домов (id, number, dscrptn) |
| `address` | Адреса — связь улицы и здания (id, street_id, bldng_id) |
| `household` | Домохозяйства (id, addrss_id) |
| `inhabitant` | Жители (id, name, phone, hh_id, is_master boolean) |
| `events` | Платёжные периоды/события (id, name, cost) |
| `payment` | Поступления денег (id, hh_id, paydate, evnt_id) |

### Модификации существующих таблиц

| Таблица | Изменение |
|---------|-----------|
| `payment` | **+ `amount` numeric** — индивидуальная сумма платежа |
| `inhabitant` | **+ `is_master boolean`** — флаг "контактного" жителя домохозяйства (вместо FK `household.master_inh_id`) |
| `household` | **− `master_inh_id`** — поле удаляется (циклический FK с inhabitant.hh_id), мастер определяется через `inhabitant.is_master = true` |

### Новые таблицы

| Таблица | Описание | Поля |
|---------|----------|------|
| `expense` | Расходы | id identity, event_id integer FK, amount numeric, date date, comment varchar |
| `useful_contact_info` | Полезные контакты (врачи, квартальная) | id identity, type varchar, contact_info text, comment varchar |
| `balance_view` | VIEW текущего остатка | `SELECT SUM(payment.amount) - SUM(expense.amount)`. Обычный VIEW (не Materialized) — данных мало, MV+триггеры избыточны и блокируют запись |

### Решённые вопросы

1. **inhabitant** — ОСТАВИТЬ. Полезна для отображения ФИО контакта дома при платежах. Мастер-житель помечается флагом `is_master boolean`.
2. **street** — ОСТАВИТЬ нормализованную схему (street → address → bldng). Данные уже есть, выпадающие списки удобнее.
3. **Остаток** — обычный `VIEW balance_view`: `SELECT SUM(payment.amount) - SUM(expense.amount)`. Materialized View с триггерами на REFRESH — антипаттерн (REFRESH MATERIALIZED VIEW берёт AccessExclusiveLock и блокирует каждый INSERT/UPDATE/DELETE). Для одного посёлка (десятки-сотни строк) обычный VIEW отрабатывает мгновенно.
4. **payment.amount** — ДОБАВИТЬ поле `amount numeric` для индивидуальной суммы каждого платежа (каждый платит свою сумму).
5. **expense** — НОВАЯ таблица расходов: `expense (id identity, event_id integer FK→events.id, amount numeric, date date, comment varchar)`.
6. **useful_contact_info** — НОВАЯ таблица полезных контактов: `useful_contact_info (id identity, type varchar, contact_info text, comment varchar)`. Типы: терапевт, педиатр, квартальная и т.д.
7. **events** — ОСТАВИТЬ как есть, без переименования.
8. **Адрес** — бабуля выбирает адрес ЦЕЛИКОМ из выпадающего списка с поиском по подстроке в реальном времени.
9. **Циклический FK** household ↔ inhabitant — РАЗОРВАН: убираем `household.master_inh_id`, добавляем `inhabitant.is_master boolean`. Остаётся один направленный FK `inhabitant.hh_id → household.id`. Это устраняет проблему порядка INSERT и упрощает каскадное удаление.
10. **Схема БД** — все таблицы проекта создаются в отдельной схеме `village` (не `public`). Это критично для ограничения AI-ассистента: text-to-SQL разрешён только по схеме `village`, системные каталоги (`pg_catalog`, `information_schema`) недоступны.

---

## Роли пользователей

| Роль | Права | Примечание |
|------|-------|------------|
| **ADMIN** | Полный доступ | Отдельный логин |
| **OPERATOR** ("бабуля") | Внесение приходов/расходов, управление событиями | Отдельный логин. Выбирает адрес целиком из выпадающего списка с поиском |
| **USER** | Только чтение | **Один общий аккаунт на всех жителей** — не для разграничения доступа, а чтобы статистику посёлка не индексировали поисковики и боты. Жителям пароль сообщается в чате/листовке |

---

## Документация

- [INTERFACE.md](INTERFACE.md) — подробное описание интерфейса и API

---

## UI для бабушки (максимально упрощённый)

### Приход
- Событие (выпадающий список из events с поиском по подстроке)
- Адрес (выпадающий список с поиском по подстроке)
- Сумма (проверка: число, ≤10000)
- Дата (календарь, дефолт — сегодня)

### Управление событиями
- Отдельный экран для оператора
- Создание нового события (название, сумма сбора)
- Просмотр списка всех событий

### Расход
- Событие (выпадающий список из events с поиском по подстроке)
- Описание работ
- Сумма (проверка: число, ≤ остаток)
- Дата (календарь, дефолт — сегодня)

---

## Функционал для жителей

- Просмотр текущего состояния сборов
- Кто сдал, кто не сдал
- Сколько осталось
- На что израсходовали

---

## Чеклист предстоящей работы

### База данных
- [x] Решить: нужна ли таблица `inhabitant`? → ОСТАВИТЬ
- [x] Решить: таблица `street` или varchar поле? → ОСТАВИТЬ нормализацию
- [x] Решить: как хранить "текущий остаток"? → обычный VIEW `balance_view`
- [x] Решить: сумма платежа → добавить `payment.amount`
- [x] Решить: циклический FK household ↔ inhabitant → убрать `household.master_inh_id`, добавить `inhabitant.is_master`
- [ ] Создать схему `village` (CREATE SCHEMA village; SET search_path = village)
- [ ] Написать Liquibase changelog **с нуля** под итоговую нормализованную схему (НЕ загружать [DB.sql](DB.sql) — это reference-дамп, не для импорта)
- [ ] Таблицы: `street`, `bldng`, `address`, `household` (без master_inh_id), `inhabitant` (+ is_master), `events`, `payment` (+ amount, + FK на events), `expense`, `useful_contact_info`
- [ ] Создать VIEW `balance_view` (SUM payments - SUM expenses)
- [ ] Добавить индексы на FK поля
- [ ] Liquibase: набор `loadData` / SQL-fixture с тестовыми данными для dev-профиля

### Backend (Spring)
- [ ] Создать проект Spring Boot
- [ ] Настроить подключение к PostgreSQL
- [ ] Настроить Liquibase миграции
- [ ] Реализовать CRUD для household
- [ ] Реализовать CRUD для payment (с amount, выбор события + адреса)
- [ ] Реализовать CRUD для expense
- [ ] Реализовать CRUD для events (отдельный экран для оператора)
- [ ] Реализовать чтение из `balance_view`
- [ ] Аутентификация (три роли: ADMIN, OPERATOR, USER)
- [ ] API для фронтенда

### AI Функционал
- [ ] Spring AI + OpenRouter интеграция (`spring-ai-starter-model-openai` + `spring-ai-bom`)
- [ ] Text-to-SQL функция executeSql()
- [ ] Валидация SQL: только SELECT **и только по схеме `village`** (отдельная роль БД с GRANT USAGE только на `village`, без доступа к `pg_catalog`/`information_schema`)
- [ ] Запретить опасные конструкции даже в SELECT: `pg_sleep`, `FOR UPDATE`, обращения к `pg_*`/`information_schema.*`, точки с запятой, комментарии `--` и `/* */`
- [ ] System prompt (ограничение тематики, список таблиц схемы `village`)
- [ ] Чат интерфейс на главной
- [ ] История вопросов в сессии
- [ ] Логирование SQL запросов
- [ ] Statement timeout на JDBC-подключении для AI-роли (ограничить тяжёлые запросы)

### Полезная информация
- [ ] Entity `useful_contact_info` (id, type, contact_info, comment)
- [ ] CRUD для редактирования (админ/оператор)
- [ ] Отображение на главной странице

### Frontend (Thymeleaf)
- [ ] Главная страница для жителей
- [ ] Блок "Полезная информация" (врачи, квартальная)
- [ ] Блок "AI Ассистент" (чат со статистикой)
- [ ] Админ-панель для оператора
- [ ] Форма "Приход" (событие + адрес + сумма + дата)
- [ ] Форма "Расход" (описание + сумма + дата)
- [ ] Экран "Управление событиями" (список + создание)
- [ ] Таблица "Последние поступления"
- [ ] Страница "/payments" с фильтрами
- [ ] Таблица "История расходов"
- [ ] Страница "/history" по периодам
- [ ] Отображение остатка
- [ ] Мобильная адаптация

### Дополнительно
- [ ] Cron для бэкапа БД
- [ ] Логирование операций
- [ ] Валидация форм
- [ ] Мобильная адаптация (бабушка с телефона!)
- [ ] Выбор адреса целиком из выпадающего списка с поиском по подстроке

---

## Предложения

1. **Адреса** — в БД оставляем нормализацию (street + bldng + address), но **в UI** (выпадающий список оператора и любое отображение адреса жителям) выдаём уже склеенную строку `"ул. Лесная, д. 5"`. Склейка делается на уровне сервиса/маппера (`street.name || ', д. ' || bldng.number`) либо в SQL-проекции для `/api/addresses`.
2. ~~**Остаток через Materialized View** — `balance_mv` с триггером на INSERT в payment/expense~~ → ПЕРЕСМОТРЕНО на обычный `VIEW balance_view`. Причина: MV+REFRESH в триггере берёт AccessExclusiveLock на каждый INSERT, объём данных мизерный — обычный VIEW отрабатывает за миллисекунды.

---

## AI Ассистент (Spring AI + OpenRouter)

### Назначение
Чат-бот на главной странице для вопросов по статистике денежных сборов.

### Примеры вопросов
- "Какой дом сдал больше всех в этом году?"
- "Сколько собрали за последний квартал?"
- "На что потратили больше всего?"
- "Кто не платил больше 2 месяцев?"

### Техническая реализация

**Технологии:**
- Spring AI 1.1.x GA (`spring-ai-starter-model-openai` + `spring-ai-bom`)
- OpenRouter (бесплатная модель) — base-url переопределяется в application.yaml
- Text-to-SQL

**Подход:**
AI генерирует SQL запросы из естественного языка, одна функция `executeSql()` выполняет их с жёсткой валидацией (только SELECT и только по схеме `village`).

**Зачем многослойная защита (defense in depth):**
SQL сочиняет внешняя LLM, мы её не контролируем. Простой блек-лист слов вроде «strstr на DROP» обходится тривиально — `SELECT pg_sleep(60)` или `SELECT * FROM pg_stat_activity` не содержат запрещённых слов, но кладут БД или утекают данные. Поэтому строим **7 слоёв**, где главное правило: **защита на уровне БД сильнее защиты на уровне приложения** — её нельзя обойти багом в нашем коде.

**Уровни защиты (от самого надёжного к страховочному):**
1. **Отдельная схема `village`** — все таблицы проекта изолированы от `public` и системных схем.
2. **Роль БД `village_ai`** — `GRANT SELECT` только на схему `village`. DELETE/DROP/чтение чужих таблиц → `permission denied` на уровне PostgreSQL. **Главный слой.**
3. **`search_path = village`** для этой роли — нет неявного доступа к `public.*` или `pg_catalog.*`.
4. **`statement_timeout = 3s`, `lock_timeout = 1s`** — отсекает `pg_sleep`, тяжёлые JOIN'ы, `FOR UPDATE`.
5. **System prompt** — инструктирует LLM писать только SELECT по таблицам схемы village.
6. **SQL-валидация в Java** — первый токен = SELECT, запрет на `;`, `--`, `/* */`, `pg_`, `information_schema`, `FOR UPDATE`, DDL/DML.
7. **Логирование** — все запросы в лог, ловим аномалии.

Подробности реализации и SQL-скрипты: [INTERFACE.md → Text-to-SQL с валидацией](INTERFACE.md#text-to-sql-с-валидацией-spring-ai).

---

## Полезная информация на главной

### Назначение
Блок с контактной информацией для жителей (врачи, квартальная).

### Поля
- Участковый терапевт (ФИО, телефон, адрес)
- Педиатр (ФИО, телефон, адрес)
- Квартальная (название, телефон, адрес)

### Управление
- Редактируется администратором
- Отображается на главной странице для всех

---

## Архитектурные решения

На основе анализа [ByWin проекта](D:\PET\ByWin - сервис Андрея для изучения\bywin) (референс)

### Технологический стек

| Компонент | Технология | Версия |
|-----------|------------|--------|
| **Backend** | Spring Boot | 3.5.x (GA) |
| **Java** | | 25 |
| **Database** | PostgreSQL | 16+ |
| **Миграции** | Liquibase | |
| **ORM** | Spring Data JPA | |
| **Frontend** | Thymeleaf | |
| **Security** | Spring Security | |
| **Mapping** | MapStruct | 1.6+ |
| **AI** | Spring AI | 1.1.x (GA) |
| **AI Provider** | OpenRouter | Бесплатная модель |
| **Utils** | Lombok | |
| **Build** | Gradle | Kotlin DSL |
| **Docker** | + docker-compose | |
| **CI/CD** | GitHub Actions | |
| **HTTPS** | nginx-proxy + letsencrypt | Автосертификаты |

### Слоистая архитектура

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

### Структура пакетов

```
ru.village/
├── controller/              # Web контроллеры
│   ├── dto/
│   │   ├── request/        # Входящие DTO
│   │   └── response/       # Исходящие DTO
│   ├── AdminController     # Для юзера (внесение данных)
│   └── PublicController    # Для жителей (просмотр)
├── service/                 # Бизнес-логика
│   ├── income/             # Приходы
│   ├── expense/            # Расходы
│   ├── household/          # Домохозяйства
│   └── balance/            # Расчёт остатка
├── domain/                  # JPA entities
│   ├── Household
│   ├── Payment
│   ├── Expense
│   └── Event
├── repository/              # Spring Data JPA
├── mapper/                  # MapStruct
├── exception/               # Custom исключения
├── handler/                 # GlobalExceptionHandler
└── config/                  # Конфигурация
```

### Docker deploy

**Dockerfile:**
```dockerfile
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
RUN addgroup -S village && adduser -S village -G village
COPY app.jar app.jar
RUN apk add --no-cache curl
USER village
EXPOSE 8080
HEALTHCHECK CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml:**
```yaml
services:
  village-app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DATABASE_URL: jdbc:postgresql://postgres:5432/village
      DATABASE_USERNAME: village
      DATABASE_PASSWORD: ${POSTGRES_PASSWORD}
    depends_on:
      postgres:
        condition: service_healthy
    restart: unless-stopped

  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: village
      POSTGRES_USER: village
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U village -d village"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

volumes:
  postgres_data:
```

### CI/CD (GitHub Actions)

**Репозиторий приватный** — GitHub Actions работает с бесплатными лимитами минут/месяц.

**Настройка секретов:**

 Settings → Secrets and variables → Actions → New repository secret

| Секрет | Описание |
|--------|----------|
| `HOST` | сервер для деплоя |
| `USER` | SSH пользователь |
| `SSH_KEY` | приватный ключ |

**Workflow:**

```yaml
name: Build & Deploy

on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 25
        uses: actions/setup-java@v4
        with:
          java-version: '25'
          distribution: 'temurin'
      - name: Build
        run: ./gradlew build -x test --no-daemon

  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Deploy
        uses: appleboy/ssh-action@v1.0.0
        with:
          host: ${{ secrets.HOST }}
          username: ${{ secrets.USER }}
          key: ${{ secrets.SSH_KEY }}
          script: |
            cd /opt/village
            docker compose pull
            docker compose up -d
```

### Отличия от ByWin

| ByWin | Village |
|-------|---------|
| Внешнее API (PowerBI) | Нет внешних интеграций |
| Сложная синхронизация | Простой CRUD |
| Email уведомления | Не нужно |
| NTLM аутентификация | Простая форма логина |

**Вывод:** Village будет проще, но с такой же качественной архитектурой!
