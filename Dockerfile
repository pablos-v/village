FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
RUN addgroup -S village && adduser -S village -G village
COPY build/libs/app.jar app.jar
RUN apk add --no-cache curl
USER village
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
