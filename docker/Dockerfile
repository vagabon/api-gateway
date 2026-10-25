FROM eclipse-temurin:25-jre
WORKDIR /app

RUN useradd --system --no-create-home spring
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]