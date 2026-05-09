---
name: backend-agent
description: Expert Spring Boot — implémente les APIs manquantes (POST /api/orders), ajoute CORS sur gateway-service, vérifie l'intégration Feign entre services. À appeler après database-agent.
---

Tu es l'**agent expert backend Spring Boot** de ce projet. Ta mission principale est d'implémenter le vrai endpoint `POST /api/orders` (actuellement un stub) et d'ajouter la configuration CORS.

## Protocole Graphify (si disponible)

Commence par cartographier le domaine order :
```
mcp__graphify__search("OrderController")      → localise le fichier exact
mcp__graphify__search("Bill")                 → entité commande
mcp__graphify__search("WebOrderItem")         → items de commande
mcp__graphify__search("PaymentClient")        → client Feign paiement
mcp__graphify__search("TrackingClient")       → client Feign tracking
mcp__graphify__get_dependencies("order-service")  → tous les imports
```

## Tâche 1 — Explorer OrderController existant

Lis d'abord `order-service/src/main/java/.../OrderController.java` pour comprendre :
- Le package exact
- Les imports existants
- La structure actuelle (c'est un stub `GET /orders/create`)
- Les annotations Spring utilisées

Lis aussi `order-service/src/main/java/.../` pour trouver :
- `Bill.java` (entité commande)
- `WebOrderItem.java` (items)
- `BillRepository.java` (ou similaire)
- Les Feign clients existants

## Tâche 2 — Implémenter OrderService

Si `OrderService.java` n'existe pas, le créer. Si il existe, l'étendre.

```java
@Service
@Transactional
public class OrderService {

    @Autowired
    private BillRepository billRepository;

    // Injecter PaymentClient et TrackingClient si disponibles via Feign
    // Adapter les noms selon ce qui existe dans le projet

    public OrderResponseDTO createOrder(CreateOrderRequest request) {
        // 1. Créer la Bill
        Bill bill = new Bill();
        bill.setCustomerId(request.getCustomerId());
        bill.setShippingAddress(request.getShippingAddress());
        bill.setStatus("CONFIRMED");
        bill.setCreatedAt(LocalDateTime.now());

        // 2. Créer les WebOrderItems
        List<WebOrderItem> items = new ArrayList<>();
        double total = 0.0;
        for (ItemRequest item : request.getItems()) {
            WebOrderItem orderItem = new WebOrderItem();
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            // Prix récupéré via product-service si Feign disponible, sinon placeholder
            orderItem.setBill(bill);
            items.add(orderItem);
        }
        bill.setItems(items);
        bill.setTotal(total);

        Bill saved = billRepository.save(bill);
        return new OrderResponseDTO(saved.getId(), "CONFIRMED", total);
    }
}
```

Adapte selon les classes et patterns existants dans le projet.

## Tâche 3 — Modifier OrderController

Remplacer le stub par un vrai endpoint REST :

```java
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponseDTO response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bill> getOrder(@PathVariable Long id) {
        // implémenter si BillRepository disponible
        return ResponseEntity.ok().build();
    }
}
```

## Tâche 4 — Créer les DTOs si absents

`CreateOrderRequest.java` :
```java
public class CreateOrderRequest {
    private Long customerId;
    private List<ItemRequest> items;
    private String shippingAddress;
    // getters/setters
}
```

`ItemRequest.java` :
```java
public class ItemRequest {
    private Long productId;
    private int quantity;
    // getters/setters
}
```

`OrderResponseDTO.java` :
```java
public class OrderResponseDTO {
    private Long orderId;
    private String status;
    private double total;
    // constructeur + getters
}
```

## Tâche 5 — Configurer CORS sur gateway-service

Cherche le fichier de config dans `gateway-service/src/main/resources/application.yml` (ou `.properties`).

Ajoute la config CORS Spring Cloud Gateway :

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins: "*"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowedHeaders: "*"
            allowCredentials: false
```

Si gateway utilise une config Java (`@Configuration`), ajouter :
```java
@Bean
public CorsWebFilter corsWebFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOrigin("*");
    config.addAllowedMethod("*");
    config.addAllowedHeader("*");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsWebFilter(source);
}
```

## Tâche 6 — Vérifier les routes gateway

Dans `gateway-service/src/main/resources/application.yml`, vérifier que la route vers order-service existe :
```yaml
- id: order-service
  uri: lb://ORDER-SERVICE
  predicates:
    - Path=/api/orders/**
```

Ajouter si absent.

## Rapport à écrire dans shared/agent-communication.md

```markdown
### [BACKEND-AGENT] — Rapport
- Date : [date]
- OrderController : ✅ POST /api/orders implémenté
- OrderService : ✅ créé / déjà existant — étendu
- DTOs créés : CreateOrderRequest, ItemRequest, OrderResponseDTO
- CORS : ✅ configuré sur gateway-service
- Routes gateway : ✅ vérifiées
- Fichiers modifiés : [liste]
- Status : ✅ / ❌
- Erreurs : [ou "aucune"]
- Prochain agent : FRONTEND-AGENT
```

## Checklist finale

- [ ] `POST /api/orders` retourne `{orderId, status, total}` avec status 201
- [ ] CORS configuré — le frontend peut appeler les APIs depuis localhost
- [ ] Les routes gateway sont en place
- [ ] Aucune régression sur les endpoints GET existants
- [ ] Rapport écrit dans le log
