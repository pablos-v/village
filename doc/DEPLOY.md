# Village — развёртывание на свежем VPS

Канонический сценарий для opensource-юзера, который хочет поднять
Village у себя: чистый VPS, один домен, HTTPS получается автоматически
через Let's Encrypt прямо внутри `docker-compose.prod.yml`.

Если на сервере уже есть свой nginx / cPanel / FastPanel и порты 80/443
заняты — смотри [DEPLOY.merc.md](DEPLOY.merc.md): там тот же стек, но
HTTPS делает внешний nginx, а Village слушает только локально.

---

## Требования к серверу

- Ubuntu 22.04 / 24.04 LTS (Debian 12 тоже подойдёт)
- 2 GB RAM минимум (3–4 GB комфортно)
- 10 GB свободного диска
- Открытые **80** и **443** наружу (для Let's Encrypt ACME challenge и
  HTTPS трафика)
- Открытый **22** для SSH
- Домен с A-записью на IP сервера (раскатился — `dig +short твой-домен`
  показывает IP)

Опционально, но рекомендуется на слабых VPS — **swap 2 GB**, чтобы JVM
+ Postgres не упёрлись в OOM при пике.

---

## 1. Подготовить сервер

### 1.1. Swap (если RAM ≤ 3 GB)

```bash
fallocate -l 2G /swapfile
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab
sysctl -w vm.swappiness=10
echo 'vm.swappiness=10' >> /etc/sysctl.conf
```

Проверка: `free -h` показывает `Swap: 2.0Gi`.

### 1.2. Docker через официальный репозиторий

Не `apt install docker.io` и не snap — оба варианта глючат с
docker-compose плагином.

```bash
apt update && apt install -y ca-certificates curl gnupg
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
  | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg

echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo $VERSION_CODENAME) stable" \
  > /etc/apt/sources.list.d/docker.list

apt update
apt install -y docker-ce docker-ce-cli containerd.io \
  docker-buildx-plugin docker-compose-plugin
systemctl enable --now docker
docker run --rm hello-world
```

Должно вывести `Hello from Docker!`.

---

## 2. Структура проекта

`git clone .` отказывается клонировать в непустой каталог — поэтому
сначала клонируем, потом создаём подпапки.

```bash
mkdir -p /opt/village
cd /opt/village
git clone https://github.com/pablos-v/village.git .
mkdir -p backups
chmod +x scripts/*.sh
chmod 700 /opt/village /opt/village/backups
```

---

## 3. `.env` с секретами

```bash
cp .env.example .env
chmod 600 .env
nano .env
```

Обязательно заполнить (см. комментарии в файле):

- `POSTGRES_PASSWORD` — длинный случайный
- `SPRING_DATASOURCE_PASSWORD` — **тот же** что `POSTGRES_PASSWORD`
- `AI_ROLE_PASSWORD` — длинный случайный
- `SPRING_AI_DATASOURCE_PASSWORD` — **тот же** что `AI_ROLE_PASSWORD`
- `APP_SECURITY_ADMIN_PASSWORD` / `_OPERATOR_PASSWORD` / `_USER_PASSWORD` —
  пароли трёх системных аккаунтов
- `OPENROUTER_API_KEY` — ключ OpenRouter, если используешь AI-ассистента
  (без него `/api/ai/chat` вернёт 500, остальное работает)
- `DOMAIN_ADDRESS` — твой домен (например `village.example.com`)
- `ADMIN_EMAIL` — для Let's Encrypt уведомлений

Опционально:
- `APP_BRAND` / `APP_TAGLINE` — название в navbar, footer, title вкладки

Сгенерировать сильный пароль: `openssl rand -base64 24`.

Счётчик посещений (Я.Метрика и т.п.) задаётся не в `.env`, а в
`application-prod.yml` — см. следующий шаг.

---

## 3a. `application-prod.yml` (счётчик и другие prod-only override'ы)

Длинный многострочный HTML в `.env` не положить — формат не дружит с
переводами строк. Для таких значений рядом лежит `application-prod.yml`,
который монтируется в контейнер и переопределяет дефолты из
`application.yml` (профиль `prod` активирован в `docker-compose.prod.yml`).

```bash
cp application-prod.yml.example application-prod.yml
nano application-prod.yml
```

Если счётчик не нужен — оставь `counter: ''` как в шаблоне, **файл всё
равно должен существовать** (без него docker compose упадёт на volume-mount).

Файл в `.gitignore` — твой счётчик не попадает в публичный репо.

---

## 4. Первый запуск

```bash
cd /opt/village
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
docker compose -f docker-compose.prod.yml logs -f village-app
```

Жди строки `Started VillageApplication`. Liquibase накатит миграции
автоматически.

Проверка локально на сервере:

```bash
curl -fsS http://localhost:8080/actuator/health
# {"status":"UP"}
```

Снаружи откроется по HTTPS через nginx-proxy + letsencrypt-companion
автоматически (компаньон попросит сертификат у Let's Encrypt при первом
запросе на домен).

Открой `https://твой-домен/` — увидишь витрину входа Village.

---

## 5. Cron-бэкапы

```bash
# Сначала разовый ручной запуск
/opt/village/scripts/backup-db.sh /opt/village/backups
ls -lh /opt/village/backups/

# Если файл village-*.sql.gz появился — ставим cron
cat > /etc/cron.d/village << 'EOF'
# Ежедневный бэкап БД в 03:00, логи в /var/log/village-backup.log
0 3 * * * root /opt/village/scripts/backup-db.sh /opt/village/backups >> /var/log/village-backup.log 2>&1
EOF
systemctl restart cron
```

Скрипт делает `pg_dump | gzip`, хранит 30 дней, старое удаляет.

---

## 6. Внешний мониторинг (рекомендую)

[UptimeRobot](https://uptimerobot.com) бесплатно даёт 50 мониторов.

1. Регистрация → Add New Monitor:
   - Type: HTTPS
   - URL: `https://твой-домен/actuator/health`
   - Interval: 5 минут
   - Keyword Type: **Keyword exists**
   - Keyword: `"status":"UP"`
2. Add Alert Contact — твой email.

Если сайт ляжет — придёт письмо в течение 5 минут.

---

## Эксплуатация

### Логи

```bash
docker compose -f /opt/village/docker-compose.prod.yml logs -f village-app
docker compose -f /opt/village/docker-compose.prod.yml logs -f postgres
cat /var/log/village-backup.log
```

### Обновление до новой версии

Если у тебя настроен GitHub Actions с auto-deploy — обновление само
накатывается на push в `main`. Вручную:

```bash
cd /opt/village
git pull
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

### Восстановление из бэкапа

```bash
ls -lh /opt/village/backups/
/opt/village/scripts/restore-db.sh /opt/village/backups/village-YYYYMMDD-HHMMSS.sql.gz
docker compose -f /opt/village/docker-compose.prod.yml restart village-app
```

Скрипт спросит подтверждение `yes/no` перед сносом данных.

### Проверка состояния

```bash
curl -fsS https://твой-домен/actuator/health
docker compose -f /opt/village/docker-compose.prod.yml ps
free -h
df -h /
```

### Эксплуатационные actuator-endpoints

Под аутентификацией (`Basic`-логин админа):

- `https://твой-домен/actuator/info` — версия, время сборки, git
- `https://твой-домен/actuator/metrics` — JVM/Tomcat/Postgres метрики
- `https://твой-домен/actuator/health` — публично, не требует логина

---

## Безопасность

- `.env` содержит секреты → `chmod 600 .env`
- `/opt/village/` доступен только root → `chmod 700 /opt/village`
- Бэкапы тоже секретны → `chmod 700 /opt/village/backups`
- SSH на root по ключу, без пароля — стандартная гигиена
- Регулярно `apt update && apt upgrade` — патчи безопасности
- Backups лежат на том же диске что и БД. Для production-critical
  данных стоит дублировать в облако (`rclone sync` на S3, Я.Диск,
  Google Drive) — это можно настроить отдельным cron'ом.
