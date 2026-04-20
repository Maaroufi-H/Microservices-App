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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TrackingService {

    private final BehaviorEventRepository repository;
    private final ProductTrackingStatsRepository statsRepository;
    private final ObjectMapper objectMapper;

    @Autowired(required = false)
    private AppDomainContext domainContext;

    public TrackingService(BehaviorEventRepository repository,
                           ProductTrackingStatsRepository statsRepository) {
        this.repository = repository;
        this.statsRepository = statsRepository;
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

        event.setViewDurationMs(dto.getViewDurationMs());

        List<Long> path = dto.getSessionItemPath();
        if (path != null && !path.isEmpty()) {
            try {
                event.setSessionItemPathJson(objectMapper.writeValueAsString(path));
            } catch (JsonProcessingException e) {
                event.setSessionItemPathJson("[]");
            }
        }

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

        if (EventType.PRODUCT_VIEW.equals(dto.getEventType()) && dto.getProductId() != null) {
            updateProductStats(dto.getProductId(), dto.getViewDurationMs(), namespace);
        }

        // Mark session as converted when a TRANSACTION_COMPLETE event is received
        if (EventType.TRANSACTION_COMPLETE.equals(dto.getEventType())
                && dto.getSessionId() != null && !dto.getSessionId().isBlank()) {
            markSessionAsConverted(dto.getSessionId());
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

    public void markSessionAsConverted(String sessionId) {
        repository.findBySessionId(sessionId).forEach(event -> {
            event.setConverted(true);
            repository.save(event);
        });
    }

    public void closeSession(String sessionId, List<Long> itemPath, String exitEvent) {
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
