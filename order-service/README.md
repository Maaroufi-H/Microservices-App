## Running the Project with Docker

This project provides a Docker setup for building and running the Java Order Service using Docker and Docker Compose. The configuration is tailored for a Spring Boot application and uses multi-stage builds for efficient image creation.

### Project-Specific Docker Requirements
- **Java Version:** Uses Eclipse Temurin 17 (JDK for build, JRE for runtime)
- **Build Tool:** Maven Wrapper (`mvnw`) is used for building the application inside the container
- **Exposed Port:** `8084` (default Spring Boot port, as set in the Dockerfile and Compose file)

### Environment Variables
- The Dockerfile sets a default `JAVA_OPTS` for container-aware JVM memory settings:
  - `JAVA_OPTS="-XX:MaxRAMPercentage=80.0 -XX:+UseContainerSupport"`
- No additional environment variables are required by default. If you need to provide custom environment variables, you can uncomment and use the `env_file` section in the `docker-compose.yml`.

### Build and Run Instructions
1. **Build and start the service:**
   ```sh
   docker compose up --build
   ```
   This will build the Docker image using the provided `Dockerfile` and start the `java-orderservice` container.

2. **Access the service:**
   - The application will be available on [http://localhost:8084](http://localhost:8084)

### Special Configuration
- The Dockerfile creates a non-root user (`appuser`) for running the application, improving container security.
- The build skips tests for faster image creation (`mvnw package -DskipTests`).
- If your application requires additional services (e.g., a database), you can extend the `docker-compose.yml` by uncommenting and configuring the relevant sections.

### Ports
- **java-orderservice:**
  - Exposes port `8084` (mapped to host `8084`)

### Notes
- If you need to add a database (e.g., PostgreSQL), refer to the commented example in the `docker-compose.yml` and adjust as needed.
- No persistent volumes are configured by default; add them if your setup requires data persistence.

---

*This section is up to date with the current Docker and Compose configuration for this project.*