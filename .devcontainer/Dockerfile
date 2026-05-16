FROM mcr.microsoft.com/devcontainers/java:25-bookworm

RUN apt-get update && apt-get install -y --no-install-recommends \
        postgresql-client \
        make \
        nano \
 && rm -rf /var/lib/apt/lists/*

ENV LANG=ru_RU.UTF-8 \
    LC_ALL=ru_RU.UTF-8

USER vscode

RUN curl -s "https://get.sdkman.io" | bash && \
    bash -c "source /home/vscode/.sdkman/bin/sdkman-init.sh && \
    sdk install gradle && \
    sdk use gradle"
