## Running the Project with Docker

This project provides a Docker setup for building and running a Java-based Discovery Service (Eureka) using Docker Compose. The setup uses Eclipse Temurin JDK 17 for both build and runtime environments.

### Requirements
- Docker and Docker Compose installed on your system.
- No external dependencies or environment variables are required by default.

### Build and Run Instructions
1. **Build and start the service:**
   ```sh
   docker compose up --build
   ```
   This will build the application using the provided `Dockerfile` and start the `java-discoveryservice` container.

2. **Access the Eureka Dashboard:**
   - The service exposes port **8761**. After the container starts, access the Eureka dashboard at:  
     [http://localhost:8761](http://localhost:8761)

### Configuration Details
- **Java Version:** Eclipse Temurin 17 (JDK for build, JRE for runtime)
- **Ports:**
  - `8761:8761` (Eureka default port)
- **User:** The container runs as a non-root user (`appuser`) for improved security.
- **JVM Options:** The container uses container-aware memory settings via `JAVA_OPTS`.
- **Networks:** The service is attached to a custom Docker network named `backend`.

### Customization
- If you need to set environment variables, you can create a `.env` file and uncomment the `env_file` line in the `docker-compose.yml`.
- No additional configuration is required for a basic setup.

---
_This section was updated to reflect the current Docker-based setup for building and running the Discovery Service._