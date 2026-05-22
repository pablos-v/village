FROM mcr.microsoft.com/devcontainers/java:25-bookworm

# postgresql-client нужен для отладки (psql) из dev-контейнера
RUN apt-get update && apt-get install -y --no-install-recommends \
        postgresql-client \
 && rm -rf /var/lib/apt/lists/*

ENV LANG=ru_RU.UTF-8 \
    LC_ALL=ru_RU.UTF-8

USER vscode
