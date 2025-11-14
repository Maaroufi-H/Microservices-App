## Running the Project with Docker

This project includes a multi-stage Docker setup for building and running a Java 17 Spring Boot application. The provided Dockerfile and `docker-compose.yml` are tailored for this project and expose the application on port **8083**.

### Project-Specific Docker Requirements
- **Java Version:** Uses Eclipse Temurin 17 (JDK for build, JRE for runtime)
- **Build Tool:** Maven Wrapper (`mvnw`) is used for building the application
- **Exposed Port:** 8083 (as configured in `application.properties` and Dockerfile)
- **No external services** (e.g., databases) are required or configured by default

### Environment Variables
- The Dockerfile sets a default `JAVA_OPTS` for container-aware JVM memory settings:
  ```sh
  JAVA_OPTS="-XX:MaxRAMPercentage=80.0 -XX:+UseContainerSupport"
  ```
- No additional environment variables are required by default. If you need to add any, you can uncomment the `env_file` section in `docker-compose.yml` and provide a `.env` file.

### Build and Run Instructions
1. **Build and start the service:**
   ```sh
   docker compose up --build
   ```
   This will build the application JAR using Maven and run it in a container.

2. **Access the application:**
   - The service will be available at [http://localhost:8083](http://localhost:8083)

### Special Configuration
- The application runs as a non-root user (`appuser`) inside the container for improved security.
- No persistent volumes or external networks are configured, as the application does not require them by default.
- If you need to add dependencies (e.g., databases), update `docker-compose.yml` accordingly.

### Ports
- **java-paymentservice:**
  - **Host:** 8083
  - **Container:** 8083

---

_If you make changes to the application's port or add environment variables, update the Docker and Compose files accordingly._