package net.maaroufi.trackingservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.maaroufi.core.domain.AppDomainContext;
import net.maaroufi.trackingservice.dto.BehaviorEventDTO;
import net.maaroufi.trackingservice.dto.UtmParameters;
import net.maaroufi.trackingservice.entities.BehaviorEvent;
import net.maaroufi.trackingservice.entities.ProductTrackingStats;
import net.maaroufi.trackingservice.enums.EventType;
import net.maaroufi.trackingservice.repository.BehaviorEventRepository;
import net.maaroufi.trackingservice.repository.ProductTrackingStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TrackingService {

    private final BehaviorEventRepository repository;
    private final ProductTrackingStatsRepository statsRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic.behavior-events}")
    private String behaviorEventsTopic;

    @Autowired(required = false)
    private AppDomainContext domainContext;

    public TrackingService(BehaviorEventRepository repository,
                           ProductTrackingStatsRepository statsRepository,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.statsRepository = statsRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public BehaviorEvent ingestEvent(BehaviorEventDTO dto) {
        BehaviorEvent event = new BehaviorEvent();

        event.setSessionId(dto.getSessionId());
        event.setCustomerId(dto.getCustomerId());
        event.setEventType(dto.getEventType());
        event.setProductId(dto.getProductId());
        event.setSearchQuery(dto.getSearchQuery());
        event.setPageUrl(dto.getPageUrl());
        event.setClientTimestamp(dto.getClientTimestamp());
        event.setServerTimestamp(Instant.now());

        String namespace = domainContext != null ? domainContext.getNamespace() : "ecommerce";
        event.setDomainNamespace(namespace);

        // ML feature #1 — view duration
        event.setViewDurationMs(dto.getViewDurationMs());

        // ML feature #3 — session item path (stored as JSON array string)
        List<Long> path = dto.getSessionItemPath();
        if (path != null && !path.isEmpty()) {
            try {
                event.setSessionItemPathJson(objectMapper.writeValueAsString(path));
            } catch (JsonProcessingException e) {
                event.setSessionItemPathJson("[]");
            }
        }

        // ML label #2 — converted defaults to false; updated post-hoc by OrderEventConsumer
        event.setConverted(false);

        UtmParameters utm = dto.getUtm();
        if (utm != null) {
            event.setUtmSource(utm.getUtmSource());
            event.setUtmMedium(utm.getUtmMedium());
            event.setUtmCampaign(utm.getUtmCampaign());
            event.setUtmContent(utm.getUtmContent());
            event.setUtmTerm(utm.getUtmTerm());
        }

        try {
            event.setRawJson(objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            event.setRawJson("{}");
        }

        BehaviorEvent saved = repository.save(event);

        // Upsert product tracking stats for PRODUCT_VIEW events
        if (EventType.PRODUCT_VIEW.equals(dto.getEventType()) && dto.getProductId() != null) {
            updateProductStats(dto.getProductId(), dto.getViewDurationMs(), namespace);
        }

        // Publish to Kafka — enriched with domainNamespace for Azure Event Hubs routing
        try {
            String enrichedJson = objectMapper.writeValueAsString(saved);
            kafkaTemplate.send(behaviorEventsTopic, saved.getSessionId(), enrichedJson);
        } catch (JsonProcessingException e) {
            kafkaTemplate.send(behaviorEventsTopic, saved.getSessionId(), event.getRawJson());
        }

        return saved;
    }

    private void updateProductStats(Long productId, Long viewDurationMs, String namespace) {
        ProductTrackingStats stats = statsRepository.findById(productId)
                .orElse(new ProductTrackingStats(productId, namespace));
        stats.setViewCount(stats.getViewCount() + 1);
        if (viewDurationMs != null && viewDurationMs > 0) {
            stats.setTotalViewDurationMs(stats.getTotalViewDurationMs() + viewDurationMs);
        }
        stats.setLastViewedAt(Instant.now());
        statsRepository.save(stats);
    }

    /**
     * Updates all BehaviorEvents for a session, setting converted=true.
     * Called by OrderEventConsumer when a TRANSACTION_COMPLETE event is received.
     */
    public void markSessionAsConverted(String sessionId) {
        repository.findBySessionId(sessionId).forEach(event -> {
            event.setConverted(true);
            repository.save(event);
        });
    }

    /**
     * Persists the full session item path for all events of a session.
     * Called by POST /api/tracking/session/close.
     */
    public void closeSession(String sessionId, List<Long> itemPath, String exitEventType) {
        String pathJson = "[]";
        if (itemPath != null && !itemPath.isEmpty()) {
            try {
                pathJson = objectMapper.writeValueAsString(itemPath);
            } catch (JsonProcessingException e) {
                pathJson = "[]";
            }
        }
        final String finalPathJson = pathJson;
        repository.findBySessionId(sessionId).forEach(event -> {
            event.setSessionItemPathJson(finalPathJson);
            repository.save(event);
        });
    }

    public List<ProductTrackingStats> getAllProductStats() {
        return statsRepository.findAllByOrderByViewCountDesc();
    }

    public ProductTrackingStats getProductStats(Long productId) {
        return statsRepository.findById(productId).orElse(null);
    }
}
