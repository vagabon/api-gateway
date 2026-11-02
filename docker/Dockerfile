FROM eclipse-temurin:25-jre
WORKDIR /app

RUN useradd --system --no-create-home spring
USER spring

EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=60"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]