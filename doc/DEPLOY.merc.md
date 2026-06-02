# Деплой Village на `merc` (Ubuntu + FastPanel)

Сценарий: сервер уже занят сторонним nginx (FastPanel, cPanel и т.п.),
80/443 трогать нельзя. Village крутится в Docker, слушает только
`127.0.0.1:8080`, наружу его проксирует системный nginx, SSL — Let's
Encrypt через панель.

Для «чистого» VPS используй [DEPLOY.md](DEPLOY.md) (с nginx-proxy +
letsencrypt-companion внутри Docker).

## Параметры этой инсталляции

- Хост: `merc` (Ubuntu 24.04 LTS)
- Домен: `улица-зелёная.рф` (Punycode `xn--80aifda6h3aaq7c.xn--p1ai`)
- Панель: FastPanel
- Уживается с: 2 существующими сайтами на FastPanel

## 0. Предусловия

- root SSH на сервер
- DNS A-запись поддомена → IP сервера, уже раскатилась
- 4 GB RAM, 15+ GB диск

## 1. Swap 2 GB

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

## 2. Docker (официальный репо, НЕ snap)

```bash
apt update && apt install -y ca-certificates curl gnupg
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg

echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo $VERSION_CODENAME) stable" \
  > /etc/apt/sources.list.d/docker.list

apt update
apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
systemctl enable --now docker
docker run --rm hello-world
```

Проверка: `Hello from Docker!`, сторонние сайты живы.

## 3. Структура проекта

`git clone .` отказывается клонировать в непустой каталог, поэтому
сначала клонируем, потом создаём подпапки.

```bash
mkdir -p /opt/village
cd /opt/village
git clone https://github.com/pablos-v/village.git .
mkdir -p backups
chmod +x scripts/*.sh
chmod 700 /opt/village /opt/village/backups
```

## 4. `.env` с секретами

```bash
cp .env.example .env
nano .env
```

Заполнить:

```dotenv
POSTGRES_PASSWORD=<openssl rand -base64 24>
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/village
SPRING_DATASOURCE_USERNAME=village
SPRING_DATASOURCE_PASSWORD=<тот же что POSTGRES_PASSWORD>

AI_ROLE_PASSWORD=<openssl rand -base64 24>
SPRING_AI_DATASOURCE_URL=jdbc:postgresql://postgres:5432/village
SPRING_AI_DATASOURCE_USERNAME=village_ai
SPRING_AI_DATASOURCE_PASSWORD=<тот же что AI_ROLE_PASSWORD>

APP_SECURITY_ADMIN_PASSWORD=<сильный>
APP_SECURITY_OPERATOR_PASSWORD=<сильный>
APP_SECURITY_USER_PASSWORD=<запоминаемый — раздаётся жителям>

OPENROUTER_API_KEY=sk-or-v1-...

IMAGE=ghcr.io/pablos-v/village:latest
```

**НЕ заполнять** для этого сценария: `DOMAIN_ADDRESS`, `ADMIN_EMAIL`
— они нужны только в каноническом compose для letsencrypt-companion;
в FastPanel-варианте SSL делает панель.

## 4a. `application-prod.yml` (счётчик и другие prod-only override'ы)

Длинный многострочный HTML (счётчик Я.Метрики и т.п.) в `.env` положить
нельзя — формат не дружит с переводами строк. Для таких значений рядом
лежит `application-prod.yml`, который монтируется в контейнер и
переопределяет дефолты из `application.yml` (профиль `prod` активирован
в `docker-compose.merc.yml`).

```bash
cp application-prod.yml.example application-prod.yml
nano application-prod.yml
```

Если счётчик не нужен — оставь `counter: ''` как в шаблоне, **файл всё
равно должен существовать** (без него docker compose упадёт на volume-mount).

Заполнить (пример с Я.Метрикой):

```yaml
app:
  branding:
    counter: |
      <!-- Yandex.Metrika counter -->
      <script type="text/javascript">
        (function(m,e,t,r,i,k,a){ ... })(window, document, 'script', ...);
        ym(XXXXXXXX, 'init', { ... });
      </script>
      <noscript><div><img src="https://mc.yandex.ru/watch/XXXXXXXX" style="position:absolute; left:-9999px;" alt="" /></div></noscript>
      <!-- /Yandex.Metrika counter -->
```

`|` в YAML — литеральный блок, переводы строк сохраняются, escape кавычек
не требуется. Файл в `.gitignore` — твой счётчик не попадает в публичный репо.

## 5. Первый запуск

```bash
cd /opt/village
docker compose -f docker-compose.merc.yml pull
docker compose -f docker-compose.merc.yml up -d
docker compose -f docker-compose.merc.yml logs -f village-app
```

Жди строки `Started VillageApplication`. Liquibase накатит миграции
автоматически.

Проверка:

```bash
curl -s http://127.0.0.1:8080/actuator/health
# {"status":"UP"}
```

Снаружи сайт **пока недоступен** — это правильно, нужен шаг 6.

## 6. Сайт в FastPanel (reverse proxy)

В FastPanel UI:

1. **Сайты → Добавить сайт**
2. Домен: `улица-зелёная.рф` (панель сама конвертирует в Punycode для
   nginx-конфига)
3. Тип сайта: **Reverse Proxy**
4. Upstream / Backend: `http://127.0.0.1:8080`
5. Сохранить

Проверка из браузера: `http://улица-зелёная.рф` → витрина входа.

## 7. SSL (Let's Encrypt через FastPanel)

В FastPanel → Сайты → твой сайт → **SSL → Let's Encrypt → Получить**.

Если ругнётся на IDN — попробуй ввести домен в Punycode-форме
`xn--80aifda6h3aaq7c.xn--p1ai`. Сертификат покроет оба варианта.

Включи redirect HTTP → HTTPS.

Проверка: `https://улица-зелёная.рф/actuator/health` → `{"status":"UP"}`.

## 8. Cron-бэкапы

```bash
# Сначала разовая ручная проверка
/opt/village/scripts/backup-db.sh /opt/village/backups
ls -lh /opt/village/backups/

# Поставить cron
cat > /etc/cron.d/village << 'EOF'
0 3 * * * root /opt/village/scripts/backup-db.sh /opt/village/backups >> /var/log/village-backup.log 2>&1
EOF
systemctl restart cron
```

## 9. UptimeRobot (опционально)

https://uptimerobot.com → New Monitor:
- Type: HTTPS
- URL: `https://улица-зелёная.рф/actuator/health`
- Interval: 5 минут
- Keyword: `"status":"UP"`
- Alert Contact: email

## Эксплуатация

```bash
# Логи
docker compose -f /opt/village/docker-compose.merc.yml logs -f village-app

# Рестарт (например после смены .env)
docker compose -f /opt/village/docker-compose.merc.yml restart village-app

# Обновление до новой версии (после push в main → CI собрал и запушил)
cd /opt/village
docker compose -f docker-compose.merc.yml pull village-app
docker compose -f docker-compose.merc.yml up -d

# Восстановление из бэкапа
/opt/village/scripts/restore-db.sh /opt/village/backups/village-20260601-030000.sql.gz
docker compose -f /opt/village/docker-compose.merc.yml restart village-app

# Состояние
docker compose -f /opt/village/docker-compose.merc.yml ps
curl -s http://127.0.0.1:8080/actuator/health
```

## Безопасность

- `/opt/village/.env` содержит секреты — `chmod 600 .env`
- Логи могут содержать имена жителей, suммы платежей — `chmod 700 /opt/village`
- Бэкапы тоже секреты — `chmod 700 /opt/village/backups`
