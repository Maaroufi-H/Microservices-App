# auth-service — Java 21 (Virtual Threads / Loom)
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
USER appuser
EXPOSE 8087
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=80.0", \
  "-XX:+UseContainerSupport", \
  "org.springframework.boot.loader.launch.JarLauncher"]
