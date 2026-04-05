package net.maaroufi.trackingservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.maaroufi.core.domain.AppDomainContext;
import net.maaroufi.trackingservice.dto.BehaviorEventDTO;
import net.maaroufi.trackingservice.dto.UtmParameters;
import net.maaroufi.trackingservice.entities.BehaviorEvent;
import net.maaroufi.trackingservice.repository.BehaviorEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TrackingService {

    private final BehaviorEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic.behavior-events}")
    private String behaviorEventsTopic;

    @Autowired(required = false)
    private AppDomainContext domainContext;

    public TrackingService(BehaviorEventRepository repository,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
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

        // Domain namespace for ML routing
        event.setDomainNamespace(domainContext != null ? domainContext.getNamespace() : "ecommerce");

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

        // Publish to Kafka — enriched with domainNamespace for Azure Event Hubs routing
        try {
            String enrichedJson = objectMapper.writeValueAsString(saved);
            kafkaTemplate.send(behaviorEventsTopic, saved.getSessionId(), enrichedJson);
        } catch (JsonProcessingException e) {
            kafkaTemplate.send(behaviorEventsTopic, saved.getSessionId(), event.getRawJson());
        }

        return saved;
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
     * Called by POST /api/tracking/session/close (browser beforeunload or purchase confirmation).
     * Allows backfilling the sessionItemPath on events that were sent before the full path was known.
     *
     * @param sessionId      Session identifier
     * @param itemPath       Complete ordered list of productIds browsed during the session
     * @param exitEventType  The event type that triggered the session close (e.g. "PURCHASE")
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
}
