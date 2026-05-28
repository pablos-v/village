#!/usr/bin/env bash
# Восстановление БД из бэкапа. ВНИМАНИЕ: затирает текущие данные.
# Использование: restore-db.sh <backup-file.sql.gz>
set -euo pipefail

BACKUP_FILE="${1:-}"
COMPOSE_FILE="${COMPOSE_FILE:-/opt/village/docker-compose.prod.yml}"

if [ -z "$BACKUP_FILE" ] || [ ! -f "$BACKUP_FILE" ]; then
    echo "Usage: $0 <backup-file.sql.gz>"
    echo "File not found: $BACKUP_FILE"
    exit 1
fi

echo "Restoring from $BACKUP_FILE"
echo "ВНИМАНИЕ: текущие данные БД будут УДАЛЕНЫ. Подтвердить? (yes/no)"
read -r CONFIRM
if [ "$CONFIRM" != "yes" ]; then
    echo "Aborted"
    exit 1
fi

# Дроп схемы village — Liquibase накатит миграции при следующем старте
docker compose -f "$COMPOSE_FILE" exec -T postgres \
    psql -U village -d village -c "DROP SCHEMA IF EXISTS village CASCADE;"

gunzip -c "$BACKUP_FILE" | docker compose -f "$COMPOSE_FILE" exec -T postgres \
    psql -U village -d village

echo "Restore complete."
echo "Перезапусти приложение: docker compose -f $COMPOSE_FILE restart village-app"
