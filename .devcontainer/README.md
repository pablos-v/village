# Dev Container для Village

## Быстрый старт

```bash
git clone https://github.com/pablos-v/village.git
cd village
code .
```

VS Code автоматически предложит "Reopen in Container" — нажимайте.

## Что внутри

- Java 25 + Gradle
- PostgreSQL 16
- Все VS Code расширения предустановлены

## Параллельная работа с коллегой

Каждый разработчик запускает **свою изолированную среду**:

```
┌─────────────────────────────────────────┐
│  Ваш компьютер                          │
│  ┌─────────────────────────────────┐    │
│  │ Your dev container              │    │
│  │ - village-dev-pablo             │    │
│  │ - village-postgres-pablo        │    │
│  │ - gradle-cache-pablo            │    │
│  └─────────────────────────────────┘    │
│                                         │
│  ┌─────────────────────────────────┐    │
│  │ Colleague dev container         │    │
│  │ - village-dev-colleague         │    │
│  │ - village-postgres-colleague    │    │
│  │ - gradle-cache-colleague        │    │
│  └─────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

**Настройка USERNAME:**

1. Создайте файл `.devcontainer/.env`:
   ```bash
   USER=ваш_логин_без_пробелов
   ```

2. Примеры:
   - `USER=pablo` → `village-dev-pablo`, `village-postgres-pablo`
   - `USER=ivan` → `village-dev-ivan`, `village-postgres-ivan`

## Порты

| Порт | Сервис |
|------|--------|
| 8080 | Приложение (авто-forward в VS Code) |
| 5433 | PostgreSQL на хосте (внутри контейнера 5432) |

## Полезные команды

```bash
# Пересобрать контейнер (после изменений Dockerfile)
# Dev Containers: Rebuild Container

# Зайти в контейнер через терминал
# VS Code: Terminal → New Terminal

# Остановить контейнеры
# VS Code: Dev Containers: Stop Container

# Удалить volumes (сброс БД)
docker volume rm village_pgdata village_gradle-cache-ваш_логин
```

## Troubleshooting

**Контейнер не стартует:**
```bash
# Проверить что Docker запущен
docker ps

# Пересоздать контейнер
# VS Code: Command Palette → Dev Containers: Rebuild Container
```

**Конфликт портов:**
- Убедитесь что `.devcontainer/.env` создан с уникальным `USER`
- PostgreSQL на хосте доступна на порту `5433` (не `5432`)
