FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Keep the runtime process unprivileged.
RUN useradd --system --uid 10001 --create-home --shell /usr/sbin/nologin appuser \
    && chown appuser:appuser /app

COPY --chown=appuser:appuser target/jaderegent-0.0.1-SNAPSHOT.jar /app/app.jar
COPY --chown=appuser:appuser data/ /app/data/

# The application is small and normally serves at most a few active users.
# Keep heap growth bounded by the container limit and fail fast on OOM.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60.0 -XX:InitialRAMPercentage=10.0 -XX:+ExitOnOutOfMemoryError"

# Supply DB_URL, DB_USERNAME and DB_PASSWORD at runtime; never bake credentials
# into this image (for example: `docker run --env-file .env ...`).

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
