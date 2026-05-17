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
| **Backend** | Spring Boot | 3.4+ |
| **Java** | | 25 (Latest) |
| **Database** | PostgreSQL | 16+ |
| **Миграции** | Liquibase | |
| **ORM** | Spring Data JPA | |
| **Frontend** | Thymeleaf | |
| **Security** | Spring Security | |
| **Mapping** | MapStruct | 1.6+ |
| **AI** | Spring AI | Latest |
| **AI Provider** | OpenRouter | Бесплатная модель |
| **Utils** | Lombok | |
| **Build** | Gradle | Kotlin DSL |
| **Docker** | + docker-compose | |
| **CI/CD** | GitHub Actions | |
| **HTTPS** | nginx-proxy + letsencrypt | Автосертификаты |

### Dependency Management через BOM

Версии зависимостей управляются через BOM (Bill of Materials):

```kotlin
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}
```

---

## Сущности базы данных

### Существующие таблицы

| Таблица | Описание |
|---------|----------|
| `street` | Улицы (id, name) |
| `bldng` | Здания/номера домов (id, number, dscrptn) |
| `address` | Адреса — связь улицы и здания (id, street_id, bldng_id) |
| `household` | Домохозяйства (id, master_inh_id, addrss_id) |
| `inhabitant` | Жители (id, name, phone, hh_id) |
| `events` | Платёжные периоды/события (id, name, cost) |
| `payment` | Поступления денег (id, hh_id, paydate, evnt_id) |

### Модификации существующих таблиц

| Таблица | Изменение |
|---------|-----------|
| `payment` | **+ `amount` numeric** — индивидуальная сумма платежа |

### Новые таблицы

| Таблица | Описание | Поля |
|---------|----------|------|
| `expense` | Расходы | id identity, event_id integer FK, amount numeric, date date, comment varchar |
| `useful_contact_info` | Полезные контакты (врачи, квартальная) | id identity, type varchar, contact_info text, comment varchar |
| `balance_mv` | Materialized View — текущий остаток | Триггеры на INSERT/UPDATE/DELETE. После ручных правок: `REFRESH MATERIALIZED VIEW balance_mv;` |

### Решённые вопросы

1. **inhabitant** — ОСТАВИТЬ. Полезна для отображения ФИО контакта дома при платежах.
2. **street** — ОСТАВИТЬ нормализованную схему (street → address → bldng). Данные уже есть, выпадающие списки удобнее.
3. **Остаток** — Materialized View `balance_mv`. Триггеры на INSERT/UPDATE/DELETE для payment и expense вызывают `REFRESH MATERIALIZED VIEW balance_mv`. После ручных правок в БД админу нужно выполнить: `REFRESH MATERIALIZED VIEW balance_mv;`
4. **payment.amount** — ДОБАВИТЬ поле `amount numeric` для индивидуальной суммы каждого платежа (каждый платит свою сумму).
5. **expense** — НОВАЯ таблица расходов: `expense (id identity, event_id integer FK→events.id, amount numeric, date date, comment varchar)`.
6. **useful_contact_info** — НОВАЯ таблица полезных контактов: `useful_contact_info (id identity, type varchar, contact_info text, comment varchar)`. Типы: терапевт, педиатр, квартальная и т.д.
7. **events** — ОСТАВИТЬ как есть, без переименования.
8. **Адрес** — бабуля выбирает адрес ЦЕЛИКОМ из выпадающего списка с поиском по подстроке в реальном времени.

---

## Роли пользователей

| Роль | Права | Примечание |
|------|-------|------------|
| **ADMIN** | Полный доступ | |
| **OPERATOR** ("бабуля") | Внесение приходов/расходов, управление событиями | Выбирает адрес целиком из выпадающего списка с поиском |
| **USER** | Только чтение | Один аккаунт на всех жителей |

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
- [x] Решить: как хранить "текущий остаток"? → Materialized View + триггер
- [x] Решить: сумма платежа → добавить `payment.amount`
- [ ] Добавить `payment.amount` numeric
- [ ] Создать таблицу `expense (id, event_id FK, amount, date, comment)`
- [ ] Создать таблицу `useful_contact_info (id, type, contact_info, comment)`
- [ ] Создать Materialized View `balance_mv` + триггеры на INSERT/UPDATE/DELETE для payment и expense
- [ ] Добавить индексы на FK поля
- [ ] Наполнить БД тестовыми данными

### Backend (Spring)
- [ ] Создать проект Spring Boot
- [ ] Настроить подключение к PostgreSQL
- [ ] Настроить Liquibase миграции
- [ ] Реализовать CRUD для household
- [ ] Реализовать CRUD для payment (с amount, выбор события + адреса)
- [ ] Реализовать CRUD для expense
- [ ] Реализовать CRUD для events (отдельный экран для оператора)
- [ ] Реализовать чтение из balance_mv
- [ ] Аутентификация (три роли: ADMIN, OPERATOR, USER)
- [ ] API для фронтенда

### AI Функционал
- [ ] Spring AI + OpenRouter интеграция
- [ ] Text-to-SQL функция executeSql()
- [ ] Валидация SQL (только SELECT)
- [ ] System prompt (ограничение тематики)
- [ ] Чат интерфейс на главной
- [ ] История вопросов в сессии
- [ ] Логирование SQL запросов

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

1. ~~**Адреса одной строкой** — вместо "Улица + Дом" хранить полный адрес "ул. Лесная, д. 5"~~ → ОТКЛОНЕНО, оставляем нормализацию
2. **Остаток через Materialized View** — `balance_mv` с триггером на INSERT в payment/expense → ПРИНЯТО

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
- Spring AI
- OpenRouter (бесплатная модель)
- Text-to-SQL

**Подход:**
AI генерирует SQL запросы из естественного языка, одна функция `executeSql()` выполняет их с жёсткой валидацией (только SELECT).

**Уровни защиты:**
1. **System prompt** — ограничивает тематику
2. **SQL validation** — только SELECT запросы
3. **Forbidden words** — блокировка DROP, DELETE и т.д.
4. **Injection check** — защита от SQL инъекций
5. **Logging** — мониторинг всех запросов

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
| **Backend** | Spring Boot | 3.4+ |
| **Java** | | 25 (Latest) |
| **Database** | PostgreSQL | 16+ |
| **Миграции** | Liquibase | |
| **ORM** | Spring Data JPA | |
| **Frontend** | Thymeleaf | |
| **Security** | Spring Security | |
| **Mapping** | MapStruct | 1.6+ |
| **AI** | Spring AI | Latest |
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
│   └── Period
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
