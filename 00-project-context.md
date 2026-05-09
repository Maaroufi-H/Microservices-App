# Contexte Projet — Microservices E-commerce Spring Boot

## Dépôt & Branche de travail

```
git clone https://github.com/Maaroufi-H/Microservices-App.git
cd Microservices-App
git checkout claude/ml-product-recommendations-gB1Vo
```

---

## Architecture — 12 services

| Service | Port | Tech | Rôle |
|---|---|---|---|
| `discovery-service` | 8761 | Spring Eureka | Registre de services |
| `gateway-service` | 8088 | Spring Cloud Gateway | Point d'entrée unique + static files |
| `customer-service` | 8082 | Spring Boot + JPA | Gestion clients |
| `product-service` | 8081 | Spring Boot + JPA | Catalogue produits |
| `Inventory-service` | 8080 | Spring Boot + JPA | Stocks |
| `order-service` | 8084 | Spring Boot + JPA | Commandes (Bill, WebOrderItem) |
| `payment-service` | 8083 | Spring Boot + JPA | Paiements |
| `tracking-service` | 8085 | Spring Boot + JPA | Comportements utilisateurs |
| `recommendation-service` | 8086 | Spring Boot + Azure ML | Recommandations produits |
| `monitoring-service` | 8087 | Spring Boot | Stats produits (ProductTrackingStats) |
| `geolocation-service` | 8090 | Spring Boot + Nominatim | Géolocalisation (Caffeine cache) |
| `claude-assistant-service` | 8091 | Spring Boot + Anthropic API | Chat IA (SSE) |

Communication inter-services : **Feign REST** (Kafka supprimé en `b3ef9cd`)  
Base de données dev : **H2 in-memory** (PostgreSQL pour production Railway)

---

## Historique Git clé

| Commit | Résumé |
|---|---|
| `859f8fa` | First commit — customer, product, order, payment, tracking |
| `b12fa55` | ML pipeline + recommendation-service (Azure ML + fallback popular) |
| `8e78bde` | core-abstractions JAR (IProduct, ICustomer, IOrder, IBehaviorEvent) |
| `1998966` | ProductTrackingStats + monitoring-service + geolocation-service (base) |
| `89b7317` | geolocation-service refonte (Nominatim, Caffeine cache, Swagger) |
| `b2d5907` | fix forwardGeocode — catch 403 Nominatim |
| `ec25c45` | .gitignore |
| `b3ef9cd` | Migration H2→PostgreSQL sur 7 services + suppression Kafka (REST/Feign) |
| `36433aa` | claude-assistant-service (Anthropic API, chat UI SSE) |
| `7eebfb2` | README.md + docs/schema.sql |

---

## Objectif E2E

```
Browse produits → Ajouter au panier → Formulaire client → POST /api/orders → Paiement → Confirmation
```

Flux d'appels :
1. `GET /api/products` — liste des produits
2. (localStorage) — panier côté client
3. `POST /api/orders` — `{customerId, items:[{productId, qty}], shippingAddress}`
4. `POST /api/payment/process` — `{orderId, amount, method}`
5. `GET /api/tracking/sessions/{sessionId}` — tracking comportemental

---

## État actuel du code (points d'attention)

- **OrderController** : stub `GET /orders/create?orderId=&sessionId=` → à remplacer par un vrai `POST /api/orders`
- **Frontend** : néant dans gateway-service/static — à créer
- **H2** : les services utilisent PostgreSQL en `application.properties` → DATABASE-AGENT doit switcher
- **CORS** : non configuré sur gateway-service → BACKEND-AGENT doit l'ajouter

---

## Carte des Agents

| Agent | Fichier | Domaine |
|---|---|---|
| Chef d'équipe | `team-lead-agent.md` | Vision transverse, approbation user, décisions architecturales |
| Orchestrateur | `orchestrator.md` | Dispatch tactique, séquençage, lecture/écriture du log |
| Backend | `backend-agent.md` | Spring Boot APIs, OrderService, PaymentService, CORS |
| Frontend | `frontend-agent.md` | HTML/JS/CSS — shop, panier, checkout, confirmation |
| Database | `database-agent.md` | H2 config, pom.xml, entités JPA |
| DevOps | `devops-agent.md` | Scripts build/start/stop, ports, .env.local |
| Testing | `testing-agent.md` | Tests E2E curl, données de test, rapport final |
| Versioning | `versioning-agent.md` | Git branches, commits atomiques, push, tags |

Fichier de communication partagé : `shared/agent-communication.md`

---

## Flux de communication entre agents

```
TEAM-LEAD
  └─→ lit shared/agent-communication.md
  └─→ écrit [TEAM-LEAD] Plan d'exécution dans le log
  └─→ délègue à ORCHESTRATEUR

ORCHESTRATEUR
  └─→ écrit instructions pour DATABASE-AGENT dans le log
  └─→ appelle DATABASE-AGENT
  └─→ lit rapport DATABASE-AGENT
  └─→ écrit instructions pour BACKEND-AGENT...
  └─→ etc.

AGENT SPÉCIALISÉ
  └─→ lit ses instructions dans le log
  └─→ exécute sa mission
  └─→ écrit [NOM-AGENT] Rapport dans le log
  └─→ rend la main à l'ORCHESTRATEUR
```

**Règle absolue : jamais deux agents ne modifient le même fichier simultanément.**

---

## Protocole Graphify (économie de tokens)

Si Graphify MCP est disponible (`mcp__graphify__*`), **l'appeler EN PREMIER** avant toute lecture de fichier :

```
mcp__graphify__search("OrderController")   → chemin + méthodes + dépendances en 1 appel
mcp__graphify__get_dependencies("order-service")   → carte des imports Feign
mcp__graphify__find_usages("Bill")   → où Bill est instancié
```

Ne lire un fichier complet (outil Read) que si Graphify ne suffit pas.

---

## Fichiers critiques

| Fichier | Agent responsable | Action |
|---|---|---|
| `*/src/main/resources/application.properties` (7 services) | DATABASE-AGENT | PostgreSQL → H2 |
| `*/pom.xml` (7 services) | DATABASE-AGENT | h2 runtime scope |
| `order-service/.../OrderController.java` | BACKEND-AGENT | Stub → POST /api/orders |
| `gateway-service/.../application.yml` | BACKEND-AGENT | CORS config |
| `gateway-service/src/main/resources/static/` | FRONTEND-AGENT | SPA e-commerce |
| `scripts/build-all.sh` | DEVOPS-AGENT | Build Maven |
| `scripts/start-local.sh` | DEVOPS-AGENT | Startup ordonné |
