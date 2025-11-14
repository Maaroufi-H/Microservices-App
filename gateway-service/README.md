## Running the Project with Docker

This project includes a Docker setup for building and running the Java Gateway Service using Eclipse Temurin JDK 17. The provided Dockerfile and `docker-compose.yml` are tailored for this application.

### Project-Specific Docker Requirements
- **Base Image:** Uses `eclipse-temurin:17-jdk` for building and `eclipse-temurin:17-jre` for running the application.
- **Build Tool:** Maven Wrapper (`mvnw`) is used for building the project inside the container.
- **Ports:** The application listens on port **8088** (as defined in `application.properties` and exposed in the Dockerfile and Compose file).
- **User:** Runs as a non-root user (`appuser`) for improved security.

### Environment Variables
- **JAVA_OPTS:** Set to `-XX:MaxRAMPercentage=80.0` by default for container-aware JVM memory settings.
- **Additional Environment Variables:** If you need to provide custom environment variables, you can uncomment and use the `env_file` section in `docker-compose.yml`.

### Build and Run Instructions
1. **Build and start the service:**
   ```sh
   docker compose up --build
   ```
   This will build the Docker image and start the `java-gatewayservice` container.

2. **Accessing the Service:**
   - The service will be available on [http://localhost:8088](http://localhost:8088)

### Special Configuration
- **Network:** The service is attached to a custom Docker network `gateway-net` (defined in `docker-compose.yml`).
- **No additional configuration is required** unless you want to provide environment variables via a `.env` file.

### Exposed Ports
- **8088:** The application is exposed on port 8088 (host:container mapping is `8088:8088`).

---

_If you have a `HELP.md` file, refer to it for further project-specific usage details._