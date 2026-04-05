package net.maaroufi.trackingservice.entities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import net.maaroufi.core.tracking.CoreEventType;
import net.maaroufi.core.tracking.IBehaviorEvent;
import net.maaroufi.core.tracking.IUtmContext;
import net.maaroufi.trackingservice.enums.EventType;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "behavior_events")
public class BehaviorEvent implements IBehaviorEvent, IUtmContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    private Long customerId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private Long productId;
    private String searchQuery;
    private String pageUrl;

    // UTM parameters (from Facebook ads / paid campaigns)
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private String utmContent;
    private String utmTerm;

    private Instant clientTimestamp;
    private Instant serverTimestamp;

    // Full event serialized as JSON – used for replay and Azure ingestion
    @Column(columnDefinition = "TEXT")
    private String rawJson;

    // Domain namespace for multi-tenant ML routing ("ecommerce", "football", ...)
    private String domainNamespace;

    // ---- ML features (Phase 2e) ----

    /** ML feature #1 — time the user spent viewing the item, in milliseconds. */
    private Long viewDurationMs;

    /**
     * ML label #2 — did this session result in a transaction?
     * Set to false at ingestion; updated to true by OrderEventConsumer (delayed labeling).
     */
    private Boolean converted = false;

    /**
     * ML feature #3 — ordered list of productIds browsed in the session, stored as JSON array.
     * Example: "[12, 45, 7]"
     * Maintained client-side in sessionStorage; sent with each event.
     */
    @Column(columnDefinition = "TEXT")
    private String sessionItemPathJson;

    public BehaviorEvent() {}

    // ---- Existing getters/setters (unchanged) ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }

    public String getPageUrl() { return pageUrl; }
    public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }

    public String getUtmSource() { return utmSource; }
    public void setUtmSource(String utmSource) { this.utmSource = utmSource; }

    public String getUtmMedium() { return utmMedium; }
    public void setUtmMedium(String utmMedium) { this.utmMedium = utmMedium; }

    public String getUtmCampaign() { return utmCampaign; }
    public void setUtmCampaign(String utmCampaign) { this.utmCampaign = utmCampaign; }

    public String getUtmContent() { return utmContent; }
    public void setUtmContent(String utmContent) { this.utmContent = utmContent; }

    public String getUtmTerm() { return utmTerm; }
    public void setUtmTerm(String utmTerm) { this.utmTerm = utmTerm; }

    public Instant getClientTimestamp() { return clientTimestamp; }
    public void setClientTimestamp(Instant clientTimestamp) { this.clientTimestamp = clientTimestamp; }

    public Instant getServerTimestamp() { return serverTimestamp; }
    public void setServerTimestamp(Instant serverTimestamp) { this.serverTimestamp = serverTimestamp; }

    public String getRawJson() { return rawJson; }
    public void setRawJson(String rawJson) { this.rawJson = rawJson; }

    public String getDomainNamespace() { return domainNamespace; }
    public void setDomainNamespace(String domainNamespace) { this.domainNamespace = domainNamespace; }

    public Long getViewDurationMs() { return viewDurationMs; }
    public void setViewDurationMs(Long viewDurationMs) { this.viewDurationMs = viewDurationMs; }

    public Boolean getConverted() { return converted; }
    public void setConverted(Boolean converted) { this.converted = converted; }

    public String getSessionItemPathJson() { return sessionItemPathJson; }
    public void setSessionItemPathJson(String sessionItemPathJson) { this.sessionItemPathJson = sessionItemPathJson; }

    // ---- IBehaviorEvent contract ----

    @Override
    public CoreEventType getCoreEventType() {
        if (eventType == null) return null;
        switch (eventType) {
            case PRODUCT_VIEW:     return CoreEventType.ITEM_DETAIL_VIEW;
            case PRODUCT_CLICK:    return CoreEventType.ITEM_CLICK;
            case ADD_TO_CART:      return CoreEventType.ADD_TO_INTENT;
            case REMOVE_FROM_CART: return CoreEventType.REMOVE_FROM_INTENT;
            case PURCHASE:         return CoreEventType.TRANSACTION_COMPLETE;
            case SEARCH:           return CoreEventType.SEARCH;
            case PAGE_VIEW:        return CoreEventType.PAGE_VIEW;
            case FB_AD_CLICK:      return CoreEventType.AD_CLICK;
            default:               return CoreEventType.PAGE_VIEW;
        }
    }

    @Override
    public IUtmContext getUtmContext() {
        return this;
    }

    @Override
    public boolean isConverted() {
        return Boolean.TRUE.equals(converted);
    }

    @Override
    public List<Long> getSessionItemPath() {
        if (sessionItemPathJson == null || sessionItemPathJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return new ObjectMapper().readValue(sessionItemPathJson, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
