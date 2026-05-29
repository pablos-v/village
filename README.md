# Village

Простой учёт общественных сборов для СНТ или коттеджного посёлка. Заменяет тетрадку и доску объявлений: жители видят кассу и историю, староста (бабушка) вносит приходы и расходы с телефона.

Боевая инсталляция: **<https://улица-зелёная.рф>**

## Возможности

- 💰 Общий баланс, последние поступления и расходы — на главной
- 📋 Поступления с фильтром по году и месяцу, список расходов, история по событиям сборов
- 📝 Внесение приходов с автокомплитом адреса, расходов с защитой от перерасхода
- 📞 Полезные контакты (врач, квартальная) — управляются админом, видны жителям
- 🤖 ИИ-ассистент: вопрос на русском → SELECT-запрос → человеческий ответ. Семь слоёв защиты — отдельная роль БД (read-only), regex-валидатор SQL, system prompt, statement_timeout, и т.д.
- 🔐 Три фиксированные роли (admin / operator / user), вход для жителей — только пароль, без логина
- 📱 Мобильная адаптация
- 🇷🇺 Поддержка кириллических доменов

## Стек

Spring Boot 3.5 (Java 25) · Spring Security · Spring Data JPA · Liquibase · Thymeleaf + Bootstrap 5 · MapStruct · Spring AI 1.1 (OpenRouter) · PostgreSQL 16 · Testcontainers · Docker + GitHub Actions.

Подробнее — [doc/ARCHITECTURE.md](doc/ARCHITECTURE.md).

## Быстрый старт (локально)

Рекомендуется **DevContainer** — postgres и dev-окружение поднимаются сами.

1. Открой проект в VS Code (или Cursor) → «Reopen in Container».
2. В терминале контейнера:
   ```bash
   ./gradlew test         # все тесты под Testcontainers PostgreSQL 16
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```
3. `http://localhost:8080` → вход: `user` / `user` (житель), `operator` / `operator`, `admin` / `admin`.

Альтернатива без DevContainer — см. [.devcontainer/README.md](.devcontainer/README.md).

## Деплой в прод

Два сценария — выбирай свой:

- **Чистый VPS** (свой nginx-proxy + Let's Encrypt в Docker) — [doc/DEPLOY.md](doc/DEPLOY.md) (в работе)
- **Хост с уже занятыми 80/443** (FastPanel, cPanel, ISPmanager) — приложение слушает `127.0.0.1:8080`, HTTPS терминирует панель → [doc/DEPLOY.merc.md](doc/DEPLOY.merc.md)

CI/CD: GitHub Actions собирает образ, пушит в GHCR, деплоит по SSH, smoke-тестит `/actuator/health`. Конфиг — [.github/workflows/build-deploy.yml](.github/workflows/build-deploy.yml).

## Эксплуатация

### Бэкапы

`scripts/backup-db.sh` — `pg_dump | gzip` с ротацией 30 дней. Ставится в cron на 03:00:
```bash
0 3 * * * root /opt/village/scripts/backup-db.sh /opt/village/backups >> /var/log/village-backup.log 2>&1
```

### Восстановление

```bash
scripts/restore-db.sh /opt/village/backups/village-YYYYMMDD-HHMMSS.sql.gz
docker compose -f docker-compose.merc.yml restart village-app
```
Скрипт спросит подтверждение `yes/no`, сдропит схему, накатит дамп. Liquibase при следующем старте увидит что миграции применены и ничего не сделает.

### Здоровье

- `/actuator/health` — публичный, отдаёт `{"status":"UP"}`
- `/actuator/info`, `/actuator/metrics` — под ADMIN
- Внешний uptime: UptimeRobot (free) → keyword `"status":"UP"`

## Структура репозитория

| Что | Где |
|-----|-----|
| Документация | [doc/](doc/) — `README.md`, `ARCHITECTURE.md`, `INTERFACE.md`, `DB.sql`, `DEPLOY*.md` |
| Скрипты эксплуатации | [scripts/](scripts/) — бэкапы, восстановление |
| Docker | `Dockerfile`, `docker-compose.prod.yml` (каноник), `docker-compose.merc.yml` (FastPanel-сценарий), `docker-compose.dev.yml` (fallback для разработки без devcontainer) |
| CI | [.github/workflows/](.github/workflows/) |

## Лицензия

[MIT](LICENSE) — бери, форкай, ставь у себя в посёлке.
