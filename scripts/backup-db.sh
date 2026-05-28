#!/usr/bin/env bash
# Бэкап БД village. Запускается cron'ом ежедневно на prod-сервере.
# Использование: backup-db.sh [BACKUP_DIR]
set -euo pipefail

BACKUP_DIR="${1:-/opt/village/backups}"
COMPOSE_FILE="${COMPOSE_FILE:-/opt/village/docker-compose.prod.yml}"
RETENTION_DAYS="${RETENTION_DAYS:-30}"
TIMESTAMP=$(date +%Y%m%d-%H%M%S)
BACKUP_FILE="$BACKUP_DIR/village-$TIMESTAMP.sql.gz"

mkdir -p "$BACKUP_DIR"

echo "Backing up to $BACKUP_FILE"
docker compose -f "$COMPOSE_FILE" exec -T postgres \
    pg_dump -U village village | gzip > "$BACKUP_FILE"

echo "Backup size: $(du -h "$BACKUP_FILE" | cut -f1)"

# Ротация: удаляем файлы старше RETENTION_DAYS
find "$BACKUP_DIR" -name "village-*.sql.gz" -mtime "+$RETENTION_DAYS" -delete

echo "Cleanup complete. Current backups:"
ls -lh "$BACKUP_DIR" | tail -n 10
