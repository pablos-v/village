FROM mcr.microsoft.com/devcontainers/java:25-bookworm

# postgresql-client нужен для отладки (psql) из dev-контейнера
RUN apt-get update && apt-get install -y --no-install-recommends \
        postgresql-client \
 && rm -rf /var/lib/apt/lists/*

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

# Pre-create gradle cache dir owned by vscode — иначе named volume gradle-cache
# монтируется как root и `./gradlew` не может писать в кэш
RUN mkdir -p /home/vscode/.gradle && chown -R vscode:vscode /home/vscode/.gradle

USER vscode
