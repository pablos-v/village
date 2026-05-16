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

### Текущие таблицы

| Таблица | Описание |
|---------|----------|
| `household` | Домохозяйства (дома на улице) |
| `inhabitant` | Жители (под вопросом — сбор идёт по домам) |
| `payment` | Поступления денег |
| `payperiod` | Платёжные периоды (месяцы) |
| `street` | Улицы |

### Вопросы для обсуждения

1. **inhabitant** — нужна ли таблица жителей? Сбор идёт по домам.
2. **street** — отдельная таблица или просто varchar поле?
3. **Остаток** — вычислять каждый раз или хранить?

---

## Роли пользователей

| Роль | Права |
|------|-------|
| **Юзер** | Внесение приходов/расходов |
| **Админ** | Управление адресами, полный доступ |

---

## Документация

- [INTERFACE.md](INTERFACE.md) — подробное описание интерфейса и API

---

## UI для бабушки (максимально упрощённый)

### Приход
- Улица (выпадающий список)
- Дом (выпадающий список)
- Сумма (проверка: число, ≤10000)
- Дата (календарь, дефолт — сегодня)

### Расход
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
- [ ] Решить: нужна ли таблица `inhabitant`?
- [ ] Решить: таблица `street` или varchar поле?
- [ ] Решить: как хранить "текущий остаток"?
- [ ] Добавить индексы на FK поля
- [ ] Создать таблицу `expenses` (расходы)
- [ ] Наполнить БД тестовыми данными

### Backend (Spring)
- [ ] Создать проект Spring Boot
- [ ] Настроить подключение к PostgreSQL
- [ ] Реализовать CRUD для household
- [ ] Реализовать CRUD для payment
- [ ] Реализовать CRUD для expenses
- [ ] Рассчитывать остаток (приход - расход)
- [ ] Аутентификация (две роли)
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
- [ ] Entity для контактных данных
- [ ] CRUD для редактирования (админ)
- [ ] Отображение на главной странице

### Frontend (Thymeleaf)
- [ ] Главная страница для жителей
- [ ] Блок "Полезная информация" (врачи, квартальная)
- [ ] Блок "AI Ассистент" (чат со статистикой)
- [ ] Админ-панель для юзера
- [ ] Форма "Приход"
- [ ] Форма "Расход"
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

---

## Предложения

1. **Адреса одной строкой** — вместо "Улица + Дом" хранить полный адрес "ул. Лесная, д. 5"
2. **Остаток вычислять** — записей будет мало, пересчёт не замедлит работу, либо Materialized View + автorefresh через триггер

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
