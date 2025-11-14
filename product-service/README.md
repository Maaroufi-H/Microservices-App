## Running the Project with Docker

This project provides a Docker setup for building and running the Java Inventory Service using Docker and Docker Compose. Below are the project-specific instructions and requirements:

### Project-Specific Docker Requirements
- **Java Version:** Uses Eclipse Temurin 17 (JDK for build, JRE for runtime)
- **Build Tool:** Maven Wrapper (`mvnw`) is used for building the application inside the container
- **Ports:** The service exposes port **8081** (as configured in `application.properties` and Docker Compose)

### Environment Variables
- No required environment variables are specified by default in the Dockerfiles or Compose file.
- If you need to provide environment variables, you can create a `.env` file and uncomment the `env_file` line in the `docker-compose.yml`.

### Build and Run Instructions
1. **Build and start the service:**
   ```sh
   docker compose up --build
   ```
   This will build the application using the provided Dockerfile and start the `java-inventoryservice` container.

2. **Accessing the Service:**
   - The service will be available on [http://localhost:8081](http://localhost:8081)

### Special Configuration
- The application runs as a non-root user (`appuser`) inside the container for improved security.
- JVM is configured with container-aware memory settings via `JAVA_OPTS`.
- The Docker Compose file defines a custom network (`backend`) for service isolation and future extensibility.
- If you add external dependencies (e.g., databases), use the `depends_on` section in `docker-compose.yml`.

### Exposed Ports
- **java-inventoryservice:** `8081:8081` (host:container)

---

*For more details on the application, refer to the rest of this README and the `HELP.md` file.*