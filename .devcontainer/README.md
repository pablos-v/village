# Dev Container для Village

## Когда использовать

**Рекомендуется** — devcontainer даёт воспроизводимое Linux-окружение и обходит проблемы Java-сети на Windows (Hyper-V/WSL/VPN адаптеры ломают `bind(0.0.0.0)`).

**Можно не использовать** — если работаете на нативном Linux/macOS, или если ресурсы хоста ограничены. В этом случае:
- поднимаете postgres через `docker-compose.dev.yml`
- запускаете `./gradlew` локально с `JAVA_TOOL_OPTIONS="-Djava.net.preferIPv4Stack=true"` (см. `~/.gradle/gradle.properties`)

## Quickstart

```bash
git clone https://github.com/pablos-v/village.git
cd village
code .
```

VS Code предложит **Reopen in Container** — нажимайте. Первый старт займёт ~5 минут (билд образа + pull postgres).

Внутри терминала контейнера:

```bash
./gradlew test
./gradlew bootRun
```

Приложение поднимается на `http://localhost:8080` (порт проброшен на хост через `forwardPorts`).

## Архитектура

- Сервис `dev` — где работает VS Code и Gradle. Базовый образ `mcr.microsoft.com/devcontainers/java:25-bookworm`. Workspace смонтирован в `/workspaces/village`.
- Сервис `postgres` — Postgres 16. Достижим как `postgres:5432` внутри сети devcontainer. Наружу проброшен на `localhost:5433` (чтобы не конфликтовать с `docker-compose.dev.yml`, который слушает 5432).
- Volume `pgdata` — данные postgres сохраняются между перезапусками контейнера.
- Volume `gradle-cache` — Gradle dependency cache не теряется при пересборке dev-сервиса.

## Конфликт портов postgres

| Где поднимаете | Порт хоста | Кто использует |
|----------------|-----------|----------------|
| `docker-compose.dev.yml` (на хосте) | 5432 | `./gradlew` на хосте |
| `.devcontainer/docker-compose.yml` (devcontainer) | 5433 | psql/DBeaver с хоста |

Внутри devcontainer всегда обращайтесь к postgres по имени сервиса: `postgres:5432`.

## Если используете devcontainer — `docker-compose.dev.yml` не нужен

Один из них, не оба одновременно (избежать дублирования сетей/volume и путаницы).
