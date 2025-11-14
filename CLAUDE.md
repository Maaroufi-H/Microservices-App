# CLAUDE.md - Microservices Application Guide

> **Last Updated:** November 14, 2025
> **Purpose:** Guide for AI assistants working with this Spring Cloud microservices codebase

---

## Table of Contents
1. [Repository Overview](#repository-overview)
2. [Architecture & Design Patterns](#architecture--design-patterns)
3. [Technology Stack](#technology-stack)
4. [Directory Structure](#directory-structure)
5. [Service Breakdown](#service-breakdown)
6. [Development Workflow](#development-workflow)
7. [Build & Deployment](#build--deployment)
8. [Coding Conventions](#coding-conventions)
9. [Testing Strategy](#testing-strategy)
10. [Inter-Service Communication](#inter-service-communication)
11. [Configuration Management](#configuration-management)
12. [Common Tasks](#common-tasks)
13. [AI Assistant Guidelines](#ai-assistant-guidelines)

---

## Repository Overview

This is a **Spring Cloud-based e-commerce microservices application** demonstrating distributed system patterns including service discovery, API gateway, event-driven architecture, and inter-service communication.

**Project Type:** Educational/Demonstration
**Architecture:** Microservices with Service Discovery (Eureka) and API Gateway
**Services Count:** 7 microservices + infrastructure components

### Key Features
- Service registration and discovery (Netflix Eureka)
- API Gateway for routing and load balancing
- Event-driven communication (Apache Kafka)
- Synchronous inter-service calls (OpenFeign)
- Containerized deployment (Docker)
- In-memory databases per service (H2)
- REST APIs with Spring Data REST

---

## Architecture & Design Patterns

### Architectural Patterns

1. **Microservices Architecture**
   - Each service has its own database (H2 in-memory)
   - Services are independently deployable
   - Loose coupling via service discovery

2. **Service Discovery Pattern**
   - Netflix Eureka Server (discovery-service)
   - All services register with Eureka on startup
   - Dynamic service location resolution

3. **API Gateway Pattern**
   - Spring Cloud Gateway (gateway-service)
   - Single entry point for clients
   - Dynamic routing via Eureka
   - Routes: `/api/customers/**` → CUSTOMER-SERVICE, `/api/products/**` → PRODUCT-SERVICE

4. **Event-Driven Architecture**
   - Apache Kafka for asynchronous communication
   - Order events published by order-service
   - Payment events processed by payment-service

5. **Repository Pattern**
   - Spring Data JPA repositories
   - Automatic REST endpoints via Spring Data REST

6. **Database-per-Service Pattern**
   - Each service manages its own H2 database
   - Independent data schemas

### Design Patterns in Code

- **CommandLineRunner**: Data initialization on startup
- **DTO/Entity Pattern**: Transient fields for cross-service data
- **Feign Client Pattern**: Declarative REST clients
- **Builder Pattern**: Entity construction (where Lombok is used)
- **Dependency Injection**: Constructor-based injection

---

## Technology Stack

### Core Technologies
| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17 | Programming language |
| **Spring Boot** | 3.5.3 | Application framework |
| **Spring Cloud** | 2025.0.0 | Microservices infrastructure |
| **Maven** | 3.x (wrapper) | Build tool |
| **Docker** | Latest | Containerization |

### Spring Ecosystem
- **Spring Data JPA**: Database access
- **Spring Data REST**: Automatic REST endpoints
- **Spring Cloud Gateway**: API Gateway
- **Spring Cloud Netflix Eureka**: Service discovery
- **Spring Cloud OpenFeign**: Declarative REST clients
- **Spring Kafka**: Event streaming
- **Spring Boot Actuator**: Monitoring and health checks
- **Thymeleaf**: Server-side templating

### Infrastructure
- **Apache Kafka** 7.4.0: Message broker
- **Zookeeper**: Kafka coordination
- **Kafka UI**: Kafka monitoring (port 8091)
- **H2 Database**: In-memory database

### Additional Libraries
- **Lombok**: Boilerplate code reduction (optional in some services)
- **SpringDoc OpenAPI**: API documentation (Swagger)
- **Eclipse Temurin**: JDK 17 / JRE 17 (Docker images)

---

## Directory Structure

```
Microservices-App/
├── .git/                      # Git repository
├── .metadata/                 # Eclipse IDE metadata
│
├── discovery-service/         # Eureka Server (Port 8761)
│   ├── src/
│   ├── Dockerfile
│   ├── compose.yaml
│   └── pom.xml
│
├── gateway-service/           # API Gateway (Port 8088)
│   ├── src/
│   ├── Dockerfile
│   ├── compose.yaml
│   └── pom.xml
│
├── customer-service/          # Customer Management (Port 8082)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/net/maaroufi/customerservice/
│   │   │   │   ├── entities/Customer.java
│   │   │   │   ├── repository/CustomerRepository.java
│   │   │   │   ├── controller/CustomerController.java
│   │   │   │   └── CustomerServiceApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── templates/home.html
│   │   └── test/
│   ├── Dockerfile
│   ├── compose.yaml
│   └── pom.xml
│
├── product-service/           # Product Catalog (Port 8081)
│   ├── src/
│   │   ├── main/java/net/maaroufi/productservice/
│   │   │   ├── entities/Product.java
│   │   │   ├── repository/ProductRepository.java
│   │   │   └── ProductServiceApplication.java
│   │   └── resources/application.properties
│   ├── Dockerfile
│   └── pom.xml
│
├── order-service/             # Order Orchestration (Port 8084)
│   ├── src/
│   │   ├── main/java/net/maaroufi/orderservice/
│   │   │   ├── entities/
│   │   │   │   ├── Bill.java
│   │   │   │   ├── ProductItem.java
│   │   │   │   ├── Customer.java (transient)
│   │   │   │   └── Product.java (transient)
│   │   │   ├── repository/
│   │   │   ├── feign/
│   │   │   │   ├── CustomerRestClient.java
│   │   │   │   └── ProductRestClient.java
│   │   │   ├── controller/OrderController.java
│   │   │   └── OrderServiceApplication.java
│   │   └── resources/application.properties
│   ├── Dockerfile
│   └── pom.xml
│
├── payment-service/           # Payment Processing (Port 8083)
│   ├── src/
│   │   ├── main/java/net/maaroufi/
│   │   │   ├── paymentservice/
│   │   │   └── paymentlistener/PaymentListener.java
│   │   └── resources/application.properties
│   ├── Dockerfile
│   └── pom.xml
│
├── Inventory-service/         # Alternative Inventory (Incomplete)
│   ├── src/
│   └── pom.xml
│   # Note: Missing Dockerfile, port conflict with product-service
│
├── Kafka/                     # Kafka Infrastructure
│   └── docker-compose.yml     # Kafka + Zookeeper + UI
│
├── compose_all/               # Orchestration for all services
│   └── docker-compose.yml     # All services + networking
│
├── tools/                     # Development tools
│   ├── swagger.yml            # OpenAPI specification
│   └── openapi-generator-cli-*.jar
│
└── Tests/                     # Utility tests
    └── src/
```

### Standard Service Structure
Every microservice follows this pattern:
```
{service-name}/
├── src/
│   ├── main/
│   │   ├── java/net/maaroufi/{service}/
│   │   │   ├── entities/           # JPA entities
│   │   │   ├── repository/         # Spring Data repositories
│   │   │   ├── controller/         # REST controllers (if custom)
│   │   │   ├── feign/              # Feign clients (if applicable)
│   │   │   ├── config/             # Configuration classes
│   │   │   └── {Service}Application.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application.yml (optional)
│   │       └── templates/          # Thymeleaf templates (if applicable)
│   └── test/java/                  # Unit/Integration tests
├── Dockerfile                       # Multi-stage Docker build
├── compose.yaml                     # Standalone Docker Compose
├── pom.xml                          # Maven configuration
├── mvnw / mvnw.cmd                 # Maven wrapper
└── README.md                        # Service documentation
```

---

## Service Breakdown

### Infrastructure Services

#### 1. Discovery Service (Port 8761)
**Location:** `/discovery-service`
**Purpose:** Eureka Server for service registration and discovery

**Key Configuration:**
```properties
server.port=8761
eureka.client.fetch-registry=false
eureka.client.register-with-eureka=false
```

**Annotations:**
- `@EnableEurekaServer`

**Dashboard:** http://localhost:8761

---

#### 2. Gateway Service (Port 8088)
**Location:** `/gateway-service`
**Purpose:** API Gateway for routing, load balancing

**Route Configuration:**
- `/api/customers/**` → `CUSTOMER-SERVICE`
- `/api/products/**` → `PRODUCT-SERVICE`

**Key Features:**
- Dynamic service discovery via Eureka
- Load balancing across service instances
- Single entry point for external clients

**Configuration File:** `src/main/java/net/maaroufi/gatewayservice/config/GatewayConfiguration.java`

---

### Business Services

#### 3. Customer Service (Port 8082)
**Location:** `/customer-service`
**Purpose:** Customer data management

**Entity:** `Customer`
```java
- id: Long
- name: String
- surname: String
- email: String
- age: Integer
```

**Database:** H2 in-memory (`customers-db`)

**Endpoints:**
- `GET /api/customers` - List all customers (Spring Data REST)
- `GET /api/customers/{id}` - Get customer by ID
- `GET /customers/name?name={name}` - Search by name (custom)
- `GET /api/vai` - Thymeleaf UI view

**Features:**
- Spring Data REST automatic endpoints
- Custom search controller
- Thymeleaf template integration
- OpenAPI/Swagger documentation
- H2 console: `/h2-console`

**Dependencies:**
- spring-boot-starter-data-jpa
- spring-boot-starter-data-rest
- spring-boot-starter-thymeleaf
- spring-cloud-starter-netflix-eureka-client
- springdoc-openapi-starter-webmvc-ui

---

#### 4. Product Service (Port 8081)
**Location:** `/product-service`
**Purpose:** Product catalog management

**Entity:** `Product`
```java
- id: Long
- name: String
- description: String
- price: Double
- quantity: Integer
```

**Database:** H2 in-memory (`products-db`)

**Endpoints:**
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/search/by-name?name={name}` - Search by name
- `GET /api/products/search/by-name-price?name={name}&price={price}` - Complex search

**Custom Queries:** Defined in `ProductRepository` with `@RestResource`

---

#### 5. Order Service (Port 8084)
**Location:** `/order-service`
**Purpose:** Order orchestration and bill management

**Entities:**
- `Bill`: Order container (id, billingDate, customerId, customer)
- `ProductItem`: Order line item (id, productId, quantity, price, bill)
- `Customer`: Transient DTO for customer data
- `Product`: Transient DTO for product data

**Database:** H2 in-memory (`order-db`)

**Communication:**
- **Synchronous**: Feign clients for customer-service and product-service
- **Asynchronous**: Kafka producer for order-events

**Feign Clients:**
```java
@FeignClient(name = "customer-service")
interface CustomerRestClient {
    PagedModel<Customer> getAllCustomers();
    Customer getCustomerByID(@PathVariable Long id);
}

@FeignClient(name = "product-service")
interface ProductRestClient {
    PagedModel<Product> getAllProducts();
    Product getProductByID(@PathVariable Long id);
}
```

**Kafka Topic:** `order-events`

**Endpoints:**
- `POST /orders/create?orderId={id}` - Create order and publish to Kafka

**Startup Behavior:**
- Fetches all customers and products via Feign
- Creates sample bills with random product items

---

#### 6. Payment Service (Port 8083)
**Location:** `/payment-service`
**Purpose:** Asynchronous payment processing

**Database:** H2 in-memory (`payment-db`)

**Kafka Integration:**
- **Consumes:** `order-events` (group: `payment-group`)
- **Produces:** `payment-events` (PaymentProcessed/PaymentFailed)

**Listener:** `PaymentListener.java`
```java
@KafkaListener(topics = "order-events", groupId = "payment-group")
public void listen(String message) {
    // Process payment
    // Publish payment-events
}
```

**Event Flow:**
1. Receives order-events from Kafka
2. Processes payment (simulated)
3. Publishes payment result to payment-events

---

#### 7. Inventory Service (Port 8081)
**Location:** `/Inventory-service`
**Status:** INCOMPLETE ⚠️

**Issues:**
- Missing Dockerfile
- Port conflict with product-service (8081)
- Appears to be an alternative/duplicate implementation

**Recommendation:** Clarify purpose or complete implementation before use.

---

## Development Workflow

### Prerequisites
- Java 17 (JDK)
- Maven 3.x (or use included wrapper)
- Docker & Docker Compose
- Git

### Initial Setup

1. **Clone Repository**
   ```bash
   git clone <repository-url>
   cd Microservices-App
   ```

2. **Verify Java Version**
   ```bash
   java -version  # Should be Java 17
   ```

3. **Build All Services**
   ```bash
   # From root directory
   for service in discovery-service gateway-service customer-service product-service order-service payment-service; do
       cd $service
       ./mvnw clean package -DskipTests
       cd ..
   done
   ```

### Running the Application

#### Option 1: Docker Compose (Recommended)

**Start Infrastructure (Kafka):**
```bash
cd Kafka
docker compose up -d
```

**Start All Services:**
```bash
cd compose_all
docker compose up --build
```

**Services will start in this order:**
1. Eureka Server (8761)
2. Gateway Service (8088)
3. Customer Service (8082)
4. Product Service (8081)
5. Order Service (8084)
6. Payment Service (8083)

**Verify Services:**
- Eureka Dashboard: http://localhost:8761
- Kafka UI: http://localhost:8091

#### Option 2: Individual Service Development

**Start Eureka First:**
```bash
cd discovery-service
./mvnw spring-boot:run
```

**Start Other Services:**
```bash
cd customer-service
./mvnw spring-boot:run
```

**Each service must wait for Eureka to be ready (8761) before starting.**

#### Option 3: Individual Docker Containers

```bash
cd customer-service
docker compose up --build
```

### Development Cycle

1. **Make Code Changes**
   - Edit Java files in `src/main/java`
   - Update configuration in `src/main/resources`

2. **Local Testing**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Run Tests**
   ```bash
   ./mvnw test
   ```

4. **Build**
   ```bash
   ./mvnw clean package -DskipTests
   ```

5. **Rebuild Docker Image**
   ```bash
   docker compose up --build
   ```

6. **Commit Changes**
   ```bash
   git add .
   git commit -m "Description of changes"
   git push origin <branch-name>
   ```

---

## Build & Deployment

### Maven Build

**Build Single Service:**
```bash
cd {service-name}
./mvnw clean package -DskipTests
```

**Run Tests:**
```bash
./mvnw test
```

**Download Dependencies Offline:**
```bash
./mvnw dependency:go-offline
```

**Skip Tests in Build:**
```bash
./mvnw package -DskipTests
```

### Docker Build

**Multi-Stage Dockerfile Pattern:**
```dockerfile
# Stage 1: Build
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 808X
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
```

**Build Image:**
```bash
docker build -t {service-name}:latest .
```

**Run Container:**
```bash
docker run -p 8082:8082 -e EUREKA_URL=http://eureka:8761/eureka customer-service:latest
```

### Docker Compose Deployment

**Production-Like Deployment:**
```bash
cd compose_all
docker compose up -d --build
```

**View Logs:**
```bash
docker compose logs -f {service-name}
```

**Stop Services:**
```bash
docker compose down
```

**Rebuild Single Service:**
```bash
docker compose up -d --build customer-service
```

### Port Mapping Reference
| Service | Port | Purpose |
|---------|------|---------|
| discovery-service | 8761 | Eureka Dashboard |
| gateway-service | 8088 | API Gateway |
| product-service | 8081 | Product API |
| customer-service | 8082 | Customer API |
| payment-service | 8083 | Payment Processing |
| order-service | 8084 | Order Management |
| Kafka | 9092 | Kafka Broker |
| Zookeeper | 2181 | Kafka Coordination |
| Kafka UI | 8091 | Kafka Dashboard |

---

## Coding Conventions

### Package Structure
```
net.maaroufi.{service}/
├── entities/              # JPA entities (@Entity)
├── repository/            # Spring Data repositories
├── controller/            # REST controllers (optional)
├── feign/                 # Feign client interfaces
├── config/                # Configuration classes
└── {Service}Application.java  # Main class
```

### Naming Conventions

**Classes:**
- Entities: `Customer`, `Product`, `Bill`
- Repositories: `CustomerRepository`, `ProductRepository`
- Controllers: `CustomerController`, `OrderController`
- Feign Clients: `CustomerRestClient`, `ProductRestClient`
- Main Class: `{Service}Application`

**Methods:**
- Repository queries: `findByName`, `findByNameAndPrice`
- REST endpoints: Use kebab-case in URLs
- Feign methods: `getAllCustomers()`, `getCustomerByID()`

**Variables:**
- camelCase for all variables
- Descriptive names: `customerRepository`, `billRepo`, `productCollection`

### Code Style

**Imports:**
- Group imports: java.*, javax.*, org.springframework.*, net.maaroufi.*
- No wildcard imports in new code (though existing code uses them)

**Annotations:**
- Main class: `@SpringBootApplication`
- Eureka server: `@EnableEurekaServer`
- Feign clients: `@EnableFeignClients`
- Entities: `@Entity`, `@Id`, `@GeneratedValue`
- Repositories: Extend `JpaRepository<Entity, ID>`
- REST controllers: `@RestController`, `@RequestMapping`
- Kafka listeners: `@KafkaListener`

**Dependency Injection:**
- **Prefer:** Constructor injection
- **Example:**
  ```java
  @Bean
  CommandLineRunner runner(OrderRepository billRepo,
                           CustomerRestClient customerClient) {
      return args -> { /* implementation */ };
  }
  ```

**Data Initialization:**
- Use `@Bean CommandLineRunner` in main application class
- Initialize sample data on startup for development

**Lombok Usage:**
- Optional in this codebase (not all services use it)
- When used: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`

### Entity Guidelines

**JPA Entities:**
```java
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private String email;
    private Integer age;

    // Constructors
    public Customer() {}

    public Customer(String name, String surname, String email, Integer age) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.age = age;
    }

    // Getters and setters
    // toString()
}
```

**Transient DTOs:**
- Use `@Transient` for fields not persisted
- Example: Customer object in Order service

### Repository Guidelines

**Spring Data Repository:**
```java
@RepositoryRestResource
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Custom queries
    @RestResource(path = "by-name")
    List<Customer> findByName(@Param("name") String name);
}
```

**REST Path:** Automatically exposed at `/api/{entity-name}s`

### Controller Guidelines

**Custom REST Controller:**
```java
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository repository;

    // Constructor injection
    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/name")
    public List<Customer> findByName(@RequestParam String name) {
        return repository.findByName(name);
    }
}
```

### Configuration Guidelines

**Application Properties:**
```properties
# Application identity
spring.application.name=customer-service
server.port=8082

# Eureka client
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.instance.prefer-ip-address=true

# Spring Cloud Config (disabled)
spring.cloud.config.enabled=false

# H2 Database
spring.datasource.url=jdbc:h2:mem:customers-db
spring.h2.console.enabled=true

# Actuator
management.endpoints.web.exposure.include=*
```

### Comments and Documentation

**When to Comment:**
- Complex business logic
- Non-obvious configuration
- TODOs and FIXMEs

**Avoid:**
- Obvious comments (`// Get customer by ID`)
- Commented-out code (use version control)

**JavaDoc:**
- Not extensively used in current codebase
- Add for public APIs and complex methods

---

## Testing Strategy

### Current State
- **Test Framework:** JUnit 5 (Jupiter) + Spring Boot Test
- **Coverage:** Minimal (smoke tests only)
- **Test Files:** 7 test files (one per service)

### Standard Test Structure

**Location:** `src/test/java/net/maaroufi/{service}/`

**Example Test:**
```java
@SpringBootTest
class CustomerServiceApplicationTests {

    @Test
    void contextLoads() {
        // Smoke test: verifies Spring context loads
    }
}
```

### Testing Recommendations

**Unit Tests:**
```java
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerController controller;

    @Test
    void shouldFindCustomerByName() {
        // Arrange
        when(repository.findByName("John"))
            .thenReturn(List.of(new Customer("John", "Doe", "john@example.com", 30)));

        // Act
        List<Customer> result = controller.findByName("John");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John");
    }
}
```

**Integration Tests:**
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class CustomerServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateAndRetrieveCustomer() {
        // Test full flow
    }
}
```

**Running Tests:**
```bash
# All tests
./mvnw test

# Specific test class
./mvnw test -Dtest=CustomerServiceApplicationTests

# Skip tests in build
./mvnw package -DskipTests
```

### Test Data

**Use CommandLineRunner for development data:**
- Located in main application class
- Initializes sample data on startup
- Should not run in production (use profiles)

---

## Inter-Service Communication

### Communication Patterns

#### 1. Synchronous REST (OpenFeign)

**Use Case:** Order service calling Customer/Product services

**Feign Client Definition:**
```java
@FeignClient(name = "customer-service")
public interface CustomerRestClient {

    @GetMapping("/api/customers")
    PagedModel<Customer> getAllCustomers();

    @GetMapping("/api/customers/{id}")
    Customer getCustomerByID(@PathVariable("id") Long id);
}
```

**Enable Feign:**
```java
@SpringBootApplication
@EnableFeignClients
public class OrderServiceApplication { }
```

**Usage:**
```java
@Bean
CommandLineRunner runner(CustomerRestClient customerClient) {
    return args -> {
        Collection<Customer> customers =
            customerClient.getAllCustomers().getContent();
    };
}
```

**Key Points:**
- Service name matches Eureka registration
- Automatic load balancing
- HATEOAS support with `PagedModel`
- Circuit breaker patterns can be added

---

#### 2. REST via Spring Data REST

**Use Case:** Automatic CRUD endpoints

**Enable:**
```java
@RepositoryRestResource
public interface CustomerRepository extends JpaRepository<Customer, Long> { }
```

**Exposed Endpoints:**
- `GET /api/customers` - List with pagination
- `GET /api/customers/{id}` - Get by ID
- `POST /api/customers` - Create
- `PUT /api/customers/{id}` - Update
- `DELETE /api/customers/{id}` - Delete
- `GET /api/customers/search` - Discover custom queries

**Custom Queries:**
```java
@RestResource(path = "by-name")
List<Customer> findByName(@Param("name") String name);
```

**Access:** `GET /api/customers/search/by-name?name=John`

---

#### 3. Event-Driven (Kafka)

**Use Case:** Order → Payment flow

**Producer (Order Service):**
```java
@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrderEvent(String orderId) {
        kafkaTemplate.send("order-events", "OrderCreated:" + orderId);
    }
}
```

**Consumer (Payment Service):**
```java
@Component
public class PaymentListener {

    @KafkaListener(topics = "order-events", groupId = "payment-group")
    public void listen(String message) {
        System.out.println("Received: " + message);
        // Process payment
        // Publish payment-events
    }
}
```

**Configuration:**
```properties
# Kafka Producer
spring.kafka.producer.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer

# Kafka Consumer
spring.kafka.consumer.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=payment-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

**Topics:**
- `order-events`: Order created events
- `payment-events`: Payment processed/failed events

---

### Service Discovery Flow

1. Service starts and registers with Eureka (8761)
2. Eureka maintains service registry
3. Client (Feign/Gateway) queries Eureka for service location
4. Client receives service instance(s) with IP:Port
5. Client calls service directly
6. Load balancing applied if multiple instances exist

---

## Configuration Management

### Configuration Files

**application.properties** (Primary configuration)
```properties
# Service identity
spring.application.name=customer-service
server.port=8082

# Eureka client
eureka.client.service-url.defaultZone=${EUREKA_URL:http://localhost:8761/eureka}
eureka.instance.prefer-ip-address=true

# Database
spring.datasource.url=jdbc:h2:mem:customers-db
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

# Spring Cloud Config (currently disabled)
spring.cloud.config.enabled=false

# Actuator
management.endpoints.web.exposure.include=*

# Logging
logging.level.net.maaroufi=DEBUG
logging.level.org.springframework.cloud=DEBUG
```

**application.yml** (Alternative format, used in Gateway)
```yaml
spring:
  application:
    name: gateway-service
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
```

### Environment Variables

**Docker Compose:**
```yaml
environment:
  - EUREKA_URL=http://eureka-server:8761/eureka
  - SPRING_PROFILES_ACTIVE=docker
```

**Override Properties:**
```bash
# Command line
java -jar app.jar --server.port=9090

# Environment variable
export SERVER_PORT=9090
```

### Configuration Hierarchy
1. Command-line arguments
2. Environment variables
3. application.properties/yml
4. Default values

### Spring Profiles

**Define Profile:**
```properties
# application-docker.properties
eureka.client.service-url.defaultZone=http://eureka-server:8761/eureka
```

**Activate:**
```bash
# Java
java -jar app.jar --spring.profiles.active=docker

# Docker
ENV SPRING_PROFILES_ACTIVE=docker
```

---

## Common Tasks

### Adding a New Microservice

1. **Create Service Structure**
   ```bash
   mkdir new-service
   cd new-service
   # Copy pom.xml from existing service
   # Update artifactId, name, description
   ```

2. **Update Dependencies**
   ```xml
   <dependencies>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-web</artifactId>
       </dependency>
       <dependency>
           <groupId>org.springframework.cloud</groupId>
           <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
       </dependency>
       <!-- Add others as needed -->
   </dependencies>
   ```

3. **Create Main Application Class**
   ```java
   package net.maaroufi.newservice;

   @SpringBootApplication
   public class NewServiceApplication {
       public static void main(String[] args) {
           SpringApplication.run(NewServiceApplication.class, args);
       }
   }
   ```

4. **Configure Service**
   ```properties
   spring.application.name=new-service
   server.port=8085
   eureka.client.service-url.defaultZone=http://localhost:8761/eureka
   ```

5. **Create Dockerfile** (copy from existing service)

6. **Add to docker-compose.yml**
   ```yaml
   new-service:
     build: ../new-service
     ports:
       - "8085:8085"
     environment:
       - EUREKA_URL=http://eureka-server:8761/eureka
     networks:
       - spring-net
     depends_on:
       - eureka-server
   ```

7. **Test**
   ```bash
   ./mvnw spring-boot:run
   # Check Eureka dashboard
   ```

---

### Adding a New Entity

1. **Create Entity Class**
   ```java
   @Entity
   public class NewEntity {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;

       private String name;
       // fields, constructors, getters, setters
   }
   ```

2. **Create Repository**
   ```java
   @RepositoryRestResource
   public interface NewEntityRepository extends JpaRepository<NewEntity, Long> {
       // Custom queries
   }
   ```

3. **Auto-exposed at** `/api/newEntities`

---

### Adding Feign Client

1. **Add Dependency**
   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-openfeign</artifactId>
   </dependency>
   ```

2. **Enable Feign**
   ```java
   @SpringBootApplication
   @EnableFeignClients
   public class Application { }
   ```

3. **Create Feign Interface**
   ```java
   @FeignClient(name = "target-service")
   public interface TargetServiceClient {
       @GetMapping("/api/resources/{id}")
       Resource getResourceById(@PathVariable Long id);
   }
   ```

4. **Use Client**
   ```java
   @Service
   public class MyService {
       private final TargetServiceClient client;

       public MyService(TargetServiceClient client) {
           this.client = client;
       }

       public Resource fetchResource(Long id) {
           return client.getResourceById(id);
       }
   }
   ```

---

### Adding Kafka Producer

1. **Add Dependency**
   ```xml
   <dependency>
       <groupId>org.springframework.kafka</groupId>
       <artifactId>spring-kafka</artifactId>
   </dependency>
   ```

2. **Configure**
   ```properties
   spring.kafka.producer.bootstrap-servers=localhost:9092
   spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
   spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
   ```

3. **Create Producer**
   ```java
   @Service
   public class EventProducer {
       @Autowired
       private KafkaTemplate<String, String> kafkaTemplate;

       public void sendEvent(String topic, String message) {
           kafkaTemplate.send(topic, message);
       }
   }
   ```

---

### Adding Kafka Consumer

1. **Configure**
   ```properties
   spring.kafka.consumer.bootstrap-servers=localhost:9092
   spring.kafka.consumer.group-id=my-group
   spring.kafka.consumer.auto-offset-reset=earliest
   ```

2. **Create Listener**
   ```java
   @Component
   public class EventListener {
       @KafkaListener(topics = "my-topic", groupId = "my-group")
       public void listen(String message) {
           System.out.println("Received: " + message);
           // Process message
       }
   }
   ```

---

### Debugging Services

**Check Service Registration:**
```bash
# Eureka dashboard
open http://localhost:8761
```

**View Logs:**
```bash
# Docker
docker compose logs -f customer-service

# Maven
./mvnw spring-boot:run
```

**Check H2 Console:**
```bash
# Navigate to http://localhost:808X/h2-console
# JDBC URL: jdbc:h2:mem:{service}-db
# Username: sa
# Password: (empty)
```

**Check Actuator Endpoints:**
```bash
# Health
curl http://localhost:8082/actuator/health

# Info
curl http://localhost:8082/actuator/info

# All endpoints
curl http://localhost:8082/actuator
```

**Monitor Kafka:**
```bash
# Kafka UI
open http://localhost:8091

# Check topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Consume messages
docker exec -it kafka kafka-console-consumer --topic order-events --bootstrap-server localhost:9092 --from-beginning
```

---

### Troubleshooting

**Service Not Registering with Eureka:**
- Check `eureka.client.service-url.defaultZone` URL
- Ensure Eureka is running (8761)
- Verify network connectivity (Docker networks)
- Check logs for connection errors

**Feign Client Errors:**
- Verify target service is registered in Eureka
- Check service name matches Eureka registration
- Ensure endpoints exist and are accessible
- Review Feign client method signatures

**Kafka Connection Issues:**
- Verify Kafka is running (port 9092)
- Check bootstrap-servers configuration
- Ensure topics exist
- Review consumer group configuration

**Port Already in Use:**
- Change `server.port` in application.properties
- Kill process using port: `lsof -ti:8082 | xargs kill -9`
- Use different port in Docker: `8083:8082`

**Database Issues:**
- Check H2 console (if enabled)
- Review `spring.jpa.hibernate.ddl-auto` setting
- Verify entity mappings
- Check SQL logs: `logging.level.org.hibernate.SQL=DEBUG`

---

## AI Assistant Guidelines

### General Principles

1. **Understand the Architecture First**
   - This is a microservices architecture with specific patterns
   - Services communicate via Feign, REST, and Kafka
   - Each service has its own database and concerns

2. **Follow Existing Patterns**
   - Use the same structure as existing services
   - Match naming conventions
   - Apply consistent configuration

3. **Maintain Service Boundaries**
   - Don't create tight coupling between services
   - Use appropriate communication patterns
   - Respect database-per-service principle

4. **Configuration Over Code**
   - Prefer configuration changes over code changes when possible
   - Use Spring Boot properties effectively
   - Leverage Spring Cloud features

---

### When Adding New Features

**Before Writing Code:**
1. Identify which service(s) are affected
2. Understand current communication patterns
3. Check if similar functionality exists
4. Review service boundaries

**Implementation Steps:**
1. Create entities (if needed)
2. Create repositories with Spring Data
3. Add custom endpoints (if Spring Data REST insufficient)
4. Update configuration
5. Add Feign clients (if cross-service calls needed)
6. Consider event-driven alternatives for async operations
7. Write tests (currently minimal, but should improve)
8. Update Docker configuration
9. Document changes

**Testing Steps:**
1. Test service in isolation
2. Verify Eureka registration
3. Test through API Gateway
4. Validate inter-service communication
5. Check Kafka events (if applicable)
6. Verify Docker deployment

---

### Code Modification Guidelines

**DO:**
- Follow Spring Boot conventions
- Use constructor injection
- Leverage Spring Data REST when possible
- Add appropriate annotations
- Use meaningful variable names
- Include error handling
- Log important operations
- Update application.properties for new configurations
- Create Dockerfile for new services
- Add service to docker-compose.yml

**DON'T:**
- Break service isolation
- Create tight coupling
- Bypass service discovery
- Hardcode URLs or ports
- Ignore existing patterns
- Remove error handling
- Skip Docker configuration
- Forget Eureka registration

---

### Service-Specific Considerations

**Discovery Service:**
- Don't modify unless changing Eureka configuration
- Stable service, rarely needs changes

**Gateway Service:**
- Add routes for new services in GatewayConfiguration
- Use dynamic discovery when possible
- Consider load balancing needs

**Business Services:**
- Always register with Eureka
- Use H2 for development (consider production DB later)
- Expose actuator endpoints
- Follow entity-repository-controller pattern

**Kafka Integration:**
- Use clear topic names
- Document event schemas
- Handle deserialization errors
- Consider consumer groups carefully

---

### Communication Pattern Selection

**Use Synchronous REST (Feign) When:**
- Immediate response needed
- Request-response pattern
- Example: Order service fetching customer details

**Use Event-Driven (Kafka) When:**
- Asynchronous processing acceptable
- Fire-and-forget scenarios
- Multiple consumers needed
- Example: Order created → Payment processing

**Use Spring Data REST When:**
- Simple CRUD operations sufficient
- Standard REST endpoints acceptable
- No complex business logic

---

### Docker and Deployment

**When Creating Dockerfile:**
- Use multi-stage builds (build + runtime)
- Base on eclipse-temurin:17-jdk (build) and :17-jre (runtime)
- Create non-root user (appuser)
- Copy only necessary files
- Set proper EXPOSE port
- Use JVM container-aware flags
- Skip tests in Docker build

**When Updating docker-compose.yml:**
- Add to appropriate compose file (individual or compose_all)
- Include environment variables (especially EUREKA_URL)
- Add to spring-net network
- Set depends_on for startup order
- Map ports correctly (host:container)

---

### Testing Approach

**Current State:**
- Minimal testing (only smoke tests)
- Opportunity for improvement

**Recommended Testing:**
1. Unit tests for business logic
2. Integration tests for repositories
3. API tests for controllers
4. Contract tests for Feign clients
5. End-to-end tests for workflows

**When Adding Tests:**
- Use JUnit 5 (Jupiter)
- Use Spring Boot Test support
- Mock external dependencies
- Test positive and negative scenarios
- Use TestRestTemplate for API tests

---

### Configuration Management

**Properties Priority:**
1. Command-line arguments (highest)
2. Environment variables
3. application.properties
4. Default values (lowest)

**Best Practices:**
- Use environment variables for deployment-specific config
- Use application.properties for defaults
- Document custom properties
- Use Spring profiles for environment-specific config

---

### Common Pitfalls to Avoid

1. **Port Conflicts:**
   - Check port availability before assigning
   - Note: Inventory-service conflicts with product-service (both 8081)

2. **Service Names:**
   - Must match Eureka registration
   - Used in Feign clients and Gateway routes
   - Use kebab-case: `customer-service`, not `CustomerService`

3. **Database Names:**
   - Use unique H2 database names per service
   - Format: `{service}-db` (e.g., `customers-db`)

4. **Dependencies:**
   - Include spring-cloud-starter-netflix-eureka-client for all services
   - Add spring-cloud-starter-openfeign when using Feign
   - Add spring-kafka when using Kafka

5. **Startup Order:**
   - Eureka must start first
   - Gateway should start after Eureka
   - Business services can start in any order after Eureka

6. **Network Configuration:**
   - All services in Docker must be on same network (spring-net)
   - Use service names as hostnames (e.g., `customer-service`)
   - Don't use `localhost` in Docker environment

---

### Documentation Updates

**When Making Changes:**
1. Update service README.md if behavior changes
2. Update this CLAUDE.md if architecture/patterns change
3. Update API documentation (OpenAPI/Swagger)
4. Update docker-compose.yml comments
5. Add inline comments for complex logic

---

### Quick Reference Commands

**Development:**
```bash
# Build service
./mvnw clean package -DskipTests

# Run service locally
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build Docker image
docker build -t service-name:latest .

# Start all services
cd compose_all && docker compose up --build

# View logs
docker compose logs -f service-name

# Stop all services
docker compose down
```

**Debugging:**
```bash
# Check Eureka registrations
curl http://localhost:8761/eureka/apps

# Check service health
curl http://localhost:8082/actuator/health

# List Kafka topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Consume Kafka topic
docker exec -it kafka kafka-console-consumer --topic order-events --bootstrap-server localhost:9092 --from-beginning
```

---

## Additional Resources

### Internal Documentation
- Service-specific README.md files in each service directory
- OpenAPI specification: `tools/swagger.yml`
- Docker Compose configurations: `Kafka/docker-compose.yml`, `compose_all/docker-compose.yml`

### External Resources
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Spring Data REST](https://docs.spring.io/spring-data/rest/docs/current/reference/html/)
- [Netflix Eureka](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
- [Spring Cloud Gateway](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
- [Spring Cloud OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)

---

## Changelog

### 2025-11-14
- Initial CLAUDE.md creation
- Documented current architecture (7 microservices + infrastructure)
- Catalogued technology stack (Spring Boot 3.5.3, Spring Cloud 2025.0.0)
- Detailed service breakdown and communication patterns
- Established coding conventions and development workflows
- Documented build, deployment, and testing strategies
- Created AI assistant guidelines

---

## Contributing

When contributing to this project:

1. Follow the established patterns and conventions
2. Test changes locally before committing
3. Update relevant documentation
4. Ensure services register with Eureka
5. Verify Docker builds succeed
6. Add appropriate tests (even if minimal)
7. Use meaningful commit messages
8. Consider impact on other services

---

## Notes

**Project Status:** Educational/Demonstration
- Designed for learning microservices patterns
- Production readiness requires:
  - Comprehensive testing
  - Production database (replace H2)
  - Security (OAuth2, JWT)
  - Resilience patterns (circuit breakers, retries)
  - Centralized logging and monitoring
  - API versioning strategy
  - Performance optimization

**Known Issues:**
- Inventory-service incomplete (missing Dockerfile, port conflict)
- Test coverage minimal (only smoke tests)
- No authentication/authorization
- In-memory databases (data lost on restart)
- No centralized configuration server (Config Server disabled)

---

**End of CLAUDE.md**

For questions or clarifications, refer to service-specific README files or explore the codebase following the patterns outlined in this document.
