# Microservices-App

A full-stack e-commerce microservices application built with Java 17, Spring Boot 3.5.3, and Spring Cloud 2025.0.0. The platform handles product catalogues, orders, payments, behavior tracking, ML-based recommendations, geolocation, and an integrated Claude AI development assistant.

---

## Architecture Overview

```
                        ┌──────────────────────────────────────────────┐
                        │           gateway-service :8088               │
                        │         (Spring Cloud Gateway)                │
                        └─────────────────┬────────────────────────────┘
                                          │ routes all /api/** traffic
          ┌──────────────────────┬────────┴──────────┬──────────────────────┐
          │                      │                    │                      │
   ┌──────▼──────┐   ┌──────────▼───────┐  ┌────────▼──────┐  ┌───────────▼──────┐
   │  customer   │   │    product       │  │   order       │  │   payment        │
   │  :8082      │   │    :8081         │  │   :8084       │  │   :8083          │
   └─────────────┘   └──────────────────┘  └───────┬───────┘  └──────────────────┘
                                                    │ Feign
          ┌──────────────────────┬─────────────────▼──────────┬──────────────────────┐
          │                      │                             │                      │
   ┌──────▼──────┐   ┌──────────▼───────┐   ┌───────────────▼───┐  ┌───────────────▼──┐
   │  tracking   │   │ recommendation   │   │   monitoring      │  │  geolocation     │
   │  :8085      │   │    :8086         │   │     :8087         │  │    :8090         │
   └─────────────┘   └──────────────────┘   └───────────────────┘  └──────────────────┘
          ▲
          │ Feign (stats)
   ┌──────┴──────┐   ┌──────────────────┐   ┌────────────────────┐
   │  Inventory  │   │  claude-assistant│   │ discovery-service  │
   │  :8080      │   │     :8091        │   │  (Eureka) :8761    │
   └─────────────┘   └──────────────────┘   └────────────────────┘

   ┌──────────────────────────────────────────────────────────────┐
   │                  PostgreSQL (Railway addon)                   │
   │  shared database — each service owns its own tables          │
   └──────────────────────────────────────────────────────────────┘
```

### Communication patterns

- **Service discovery**: Netflix Eureka — all services register by name; Feign clients resolve by logical name
- **Synchronous calls**: Spring Cloud OpenFeign (REST/HTTP)
- **Kafka removed**: event propagation replaced by direct REST calls (order → tracking via `POST /api/tracking/convert/{sessionId}`)
- **API Gateway**: Spring Cloud Gateway routes all external traffic; internal services communicate directly via Eureka

---

## Services

| Service | Port | Description |
|---|---|---|
| `discovery-service` | 8761 | Eureka service registry — must start first |
| `gateway-service` | 8088 | Spring Cloud Gateway — single public entry point |
| `customer-service` | 8082 | Customer CRUD, Spring Data REST + Thymeleaf UI, Swagger UI |
| `product-service` | 8081 | Product catalogue with ML features (category, tags) |
| `Inventory-service` | 8080 | Inventory / stock management |
| `order-service` | 8084 | Order creation, bill management, Feign to customer + product |
| `payment-service` | 8083 | Payment processing REST endpoint |
| `tracking-service` | 8085 | Behavior event ingestion, per-product stats, session conversion tracking |
| `recommendation-service` | 8086 | Azure ML real-time endpoint + popular-products fallback |
| `monitoring-service` | 8087 | Dashboard aggregating tracking stats and service health |
| `geolocation-service` | 8090 | Reverse geocoding (Nominatim), IP geolocation, distance, Caffeine cache |
| `claude-assistant-service` | 8091 | Claude AI chat for in-app development assistance |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.3 |
| Cloud | Spring Cloud 2025.0.0 |
| Service discovery | Netflix Eureka |
| API gateway | Spring Cloud Gateway |
| REST clients | Spring Cloud OpenFeign, Spring `RestClient` |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL 15+ (Railway addon) |
| ML integration | Azure ML real-time scoring endpoint |
| AI assistant | Anthropic API — `claude-opus-4-7` |
| Cache | Caffeine (geolocation service) |
| API docs | SpringDoc OpenAPI 3 / Swagger UI |
| Build | Maven 3.9+ |
| Containerisation | Docker (eclipse-temurin:17) |
| Deployment | Railway.app |

---

## API Endpoints Reference

### customer-service `:8082`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/customers` | List all customers |
| `GET` | `/api/customers/{id}` | Get customer by ID |
| `POST` | `/api/customers` | Create customer |
| `PUT` | `/api/customers/{id}` | Update customer |
| `DELETE` | `/api/customers/{id}` | Delete customer |
| `GET` | `/customers/name?name=` | Search customers by name |

Swagger UI: `http://localhost:8082/swagger-ui/index.html`

### product-service `:8081`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/products` | List all products |
| `GET` | `/api/products/{id}` | Get product by ID |
| `POST` | `/api/products` | Create product |
| `PUT` | `/api/products/{id}` | Update product |
| `DELETE` | `/api/products/{id}` | Delete product |

### order-service `:8084`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/orders` | List all orders (bills) |
| `GET` | `/api/orders/{id}` | Get order by ID |
| `GET` | `/orders/create?orderId=&sessionId=` | Create order + notify tracking |

### payment-service `:8083`

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/payment/process` | Process a payment `{"orderId":"..."}` |
| `GET` | `/api/payment/health` | Health check |

### tracking-service `:8085`

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/tracking/event` | Ingest a behavior event |
| `GET` | `/api/tracking/stats/products` | All product stats sorted by views |
| `GET` | `/api/tracking/stats/product/{productId}` | Stats for one product |
| `POST` | `/api/tracking/convert/{sessionId}` | Mark a session as converted |
| `POST` | `/api/tracking/session/close` | Close a session |
| `GET` | `/api/tracking/health` | Health check |

**Event types**: `PRODUCT_VIEW`, `ADD_TO_CART`, `REMOVE_FROM_CART`, `CHECKOUT_START`, `TRANSACTION_COMPLETE`, `SEARCH`, `PAGE_VIEW`, `SESSION_START`, `SESSION_END`

### recommendation-service `:8086`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/recommendations/{customerId}` | ML-based recommendations for a customer |
| `GET` | `/api/recommendations/anonymous/{sessionId}` | Recommendations for anonymous session |

### monitoring-service `:8087`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/monitoring/dashboard` | Full dashboard (top products + service statuses) |
| `GET` | `/api/monitoring/products/top?limit=10` | Top N products by view count |
| `GET` | `/api/monitoring/services/status` | Health of all registered services |
| `GET` | `/api/monitoring/health` | Health check |

### geolocation-service `:8090`

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/geolocation/locate` | Reverse geocode GPS coordinates |
| `POST` | `/api/geolocation/locate-by-ip` | Geolocate an IP address |
| `GET` | `/api/geolocation/search?q=&limit=5` | Forward geocoding (text → coords) |
| `GET` | `/api/geolocation/distance?lat1=&lng1=&lat2=&lng2=` | Haversine distance |
| `GET` | `/api/geolocation/stats/countries` | Aggregate users by country |
| `GET` | `/api/geolocation/history/{customerId}` | Geolocation history for a customer |
| `GET` | `/api/geolocation/latest/{customerId}` | Latest location for a customer |
| `GET` | `/api/geolocation/health` | Health check |

Swagger UI: `http://localhost:8090/swagger-ui/index.html`

### claude-assistant-service `:8091`

| Method | Path | Description |
|---|---|---|
| `GET` | `/` | Chat UI (dark-themed web interface) |
| `POST` | `/api/claude/chat` | Single-turn chat (JSON response) |
| `POST` | `/api/claude/stream` | Streaming chat (SSE, real-time) |
| `GET` | `/api/claude/health` | Health check |

---

## Local Build

### Prerequisites

- Java 17+ (`java -version`)
- Maven 3.9+ (`mvn -version`)
- PostgreSQL 15 running locally (or use Railway database URL directly)

### 1. Clone the repository

```bash
git clone https://github.com/Maaroufi-H/Microservices-App.git
cd Microservices-App
```

### 2. Set up the PostgreSQL database

```sql
-- Connect to PostgreSQL and create the database
CREATE DATABASE railway;
```

Or export the environment variable to use an existing database:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/railway
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=your_password
```

### 3. Build each service

Each service is an independent Maven project:

```bash
# Build all services (run from project root)
for svc in discovery-service gateway-service customer-service product-service \
           Inventory-service order-service payment-service tracking-service \
           recommendation-service monitoring-service geolocation-service \
           claude-assistant-service; do
  echo "Building $svc..."
  mvn -f $svc/pom.xml package -DskipTests -q
done
```

Or build a single service:

```bash
mvn -f customer-service/pom.xml package -DskipTests
```

### 4. Start the services (in order)

**Step 1 — Discovery service (Eureka) — start first and wait ~15s**

```bash
java -jar discovery-service/target/*.jar &
sleep 15
```

**Step 2 — All other services**

```bash
java -jar gateway-service/target/*.jar &
java -jar customer-service/target/*.jar &
java -jar product-service/target/*.jar &
java -jar Inventory-service/target/*.jar &
java -jar order-service/target/*.jar &
java -jar payment-service/target/*.jar &
java -jar tracking-service/target/*.jar &
java -jar recommendation-service/target/*.jar &
java -jar monitoring-service/target/*.jar &
java -jar geolocation-service/target/*.jar &
ANTHROPIC_API_KEY=sk-ant-... java -jar claude-assistant-service/target/*.jar &
```

### 5. Verify

- Eureka dashboard: `http://localhost:8761`
- Gateway: `http://localhost:8088/api/products`
- Claude chat UI: `http://localhost:8091`

---

## Deploy to Railway.app

### Overview

Railway detects Spring Boot via Nixpacks automatically. Each service is deployed as a separate Railway service, all sharing one PostgreSQL addon.

### Prerequisites

- Railway account and CLI: `npm install -g @railway/cli && railway login`
- The repo pushed to GitHub (Railway deploys from GitHub)

### Step 1 — Create a Railway project

```bash
railway init
```

### Step 2 — Add a PostgreSQL addon

In the Railway dashboard, click **+ New** → **Database** → **PostgreSQL**. Railway will expose:

```
DATABASE_URL      → jdbc:postgresql://...
DATABASE_USERNAME → postgres  
DATABASE_PASSWORD → ...
```

Note these values — you'll use them as environment variables.

### Step 3 — Create a Railway service per microservice

For each service, from the Railway dashboard: **+ New** → **GitHub Repo** → select the repo → set the **Root Directory** to the service folder (e.g., `customer-service`).

Alternatively via CLI:

```bash
railway service create customer-service
railway link <service-id>
railway up --service customer-service
```

### Step 4 — Set environment variables

Set the following for **every data service** (customer, product, Inventory, order, payment, tracking, geolocation):

| Variable | Value |
|---|---|
| `DATABASE_URL` | Value from Railway PostgreSQL addon |
| `DATABASE_USERNAME` | Value from Railway PostgreSQL addon |
| `DATABASE_PASSWORD` | Value from Railway PostgreSQL addon |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://discovery-service.railway.internal:8761/eureka` |
| `PORT` | `8080` (Railway exposes port 8080) |

Additional variables per service:

| Service | Variable | Value |
|---|---|---|
| `recommendation-service` | `azure.ml.endpoint.url` | Your Azure ML scoring endpoint |
| `recommendation-service` | `azure.ml.endpoint.api-key` | Your Azure ML API key |
| `claude-assistant-service` | `ANTHROPIC_API_KEY` | `sk-ant-...` |

### Step 5 — Deployment order

Deploy in this order (Railway starts services concurrently, but Eureka must be ready first):

1. `discovery-service`
2. `gateway-service`
3. All other services

### Step 6 — Public domain

Only `gateway-service` needs a public Railway domain:  
**Railway dashboard → gateway-service → Settings → Networking → Generate domain**

All API calls from the outside go through `https://gateway-xxx.up.railway.app/api/...`

### Step 7 — Verify deployment

```bash
# Check all services registered in Eureka
curl https://gateway-xxx.up.railway.app/api/customers

# Claude assistant
open https://claude-assistant-xxx.up.railway.app
```

---

## Azure ML Recommendations Setup

The `recommendation-service` integrates with Azure ML for personalised product recommendations.

### Deploy the Azure ML endpoint

1. Train a collaborative filtering model on your `behavior_events` data
2. Deploy a real-time scoring endpoint on Azure ML
3. The endpoint expects this request body:

```json
{
  "customer_id": 42,
  "top_n": 10,
  "domain_namespace": "ecommerce"
}
```

4. Set the endpoint URL and API key in `recommendation-service/src/main/resources/application.properties` or via environment variables:

```properties
azure.ml.endpoint.url=${AZURE_ML_ENDPOINT_URL}
azure.ml.endpoint.api-key=${AZURE_ML_API_KEY}
```

When the Azure ML endpoint is not configured, the service falls back to returning the most popular products sorted by price descending.

---

## Claude AI Assistant Setup

The `claude-assistant-service` gives you a Claude-powered chat interface pre-loaded with full knowledge of this project's architecture.

### Configuration

Set the environment variable before starting the service:

```bash
export ANTHROPIC_API_KEY=sk-ant-api03-...
java -jar claude-assistant-service/target/*.jar
```

Or in Railway, add `ANTHROPIC_API_KEY` to the `claude-assistant-service` environment variables.

### Usage

Open `http://localhost:8091` (or your Railway URL) and chat with Claude. Example prompts:

- *"Aggiungi un endpoint per cercare prodotti per categoria"*
- *"Come posso aggiungere l'autenticazione JWT al gateway?"*
- *"Scrivi un test per TrackingService"*
- *"Come funziona il sistema di raccomandazioni?"*

The assistant knows all service names, ports, package names, and architecture decisions.

### REST API

```bash
# Non-streaming
curl -X POST http://localhost:8091/api/claude/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "How does the recommendation fallback work?"}'

# Streaming (SSE)
curl -X POST http://localhost:8091/api/claude/stream \
  -H "Content-Type: application/json" \
  -H "Accept: text/event-stream" \
  -d '{"message": "Explain the tracking service architecture"}'
```

---

## Behavior Tracking — Event Schema

Send events to `POST /api/tracking/event`:

```json
{
  "sessionId": "sess-abc123",
  "customerId": 42,
  "eventType": "PRODUCT_VIEW",
  "productId": 7,
  "viewDurationMs": 4500,
  "pageUrl": "/products/7",
  "utmSource": "google",
  "utmMedium": "cpc",
  "utmCampaign": "summer-sale",
  "domainNamespace": "ecommerce"
}
```

When an order is placed (`eventType: TRANSACTION_COMPLETE` or via the order-service), the session is automatically marked as converted, enabling ML training labels.

---

## Project Structure

```
Microservices-App/
├── discovery-service/          # Eureka server
├── gateway-service/            # Spring Cloud Gateway
├── customer-service/           # Customer management
├── product-service/            # Product catalogue
├── Inventory-service/          # Inventory / stock
├── order-service/              # Orders and billing
├── payment-service/            # Payment processing
├── tracking-service/           # Behavior analytics
├── recommendation-service/     # ML recommendations
├── monitoring-service/         # Dashboard and health
├── geolocation-service/        # GPS + IP geocoding
├── claude-assistant-service/   # Claude AI chat
├── core-abstractions/          # Shared interfaces (IProduct, ICustomer, IOrder, ...)
├── compose_all/                # Docker Compose files
├── docs/
│   └── schema.sql              # PostgreSQL CREATE TABLE statements
└── README.md
```

---

## Environment Variables Reference

| Variable | Default | Description |
|---|---|---|
| `PORT` | service-specific | HTTP port (Railway sets this automatically) |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/railway` | PostgreSQL JDBC URL |
| `DATABASE_USERNAME` | `postgres` | Database username |
| `DATABASE_PASSWORD` | *(empty)* | Database password |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://localhost:8761/eureka` | Eureka server URL |
| `ANTHROPIC_API_KEY` | *(required for AI assistant)* | Anthropic API key |
| `AZURE_ML_ENDPOINT_URL` | *(optional)* | Azure ML scoring endpoint URL |
| `AZURE_ML_API_KEY` | *(optional)* | Azure ML API key |
