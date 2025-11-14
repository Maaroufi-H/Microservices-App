## Running the Project with Docker

This project includes a multi-stage Docker setup for building and running a Java Spring Boot application using Maven and Eclipse Temurin JDK 17. The provided Docker Compose file orchestrates the build and container lifecycle.

### Project-Specific Docker Requirements
- **Java Version:** Eclipse Temurin 17 (JDK for build, JRE for runtime)
- **Build Tool:** Maven Wrapper (`mvnw`) is used for building the project inside the container
- **Exposed Port:** `8082` (application runs on this port)

### Environment Variables
- No required environment variables are specified in the Dockerfile or Docker Compose file. (The `env_file` line is commented out; add a `.env` file and uncomment if needed.)

### Build and Run Instructions
1. **Build and start the service:**
   ```sh
   docker compose up --build
   ```
   This will build the application using the Maven wrapper and run it in a container.

2. **Access the application:**
   - The service will be available at [http://localhost:8082](http://localhost:8082)

### Special Configuration
- The application runs as a non-root user (`appuser`) inside the container for improved security.
- No external services (such as databases) are required or configured by default.
- No persistent volumes or custom networks are defined, as the service is stateless by default.

### Ports
- **java-customerservice:**
  - Host: `8082` → Container: `8082`

---

_If you need to add environment variables or connect to external services, update the `docker-compose.yml` accordingly (e.g., uncomment the `env_file` or add `depends_on`)._
