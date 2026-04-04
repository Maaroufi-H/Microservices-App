package net.maaroufi.trackingservice.dto;

import net.maaroufi.trackingservice.enums.EventType;

import java.time.Instant;
import java.util.Map;

public class BehaviorEventDTO {

    // Session identifier – generated client-side and stored in localStorage
    private String sessionId;

    // Null for anonymous users (not yet logged in)
    private Long customerId;

    private EventType eventType;

    // Relevant for PRODUCT_VIEW, PRODUCT_CLICK, ADD_TO_CART, REMOVE_FROM_CART, PURCHASE
    private Long productId;

    // Relevant for SEARCH events
    private String searchQuery;

    // Full URL of the page where the event occurred
    private String pageUrl;

    // Facebook ad parameters captured from the landing URL
    private UtmParameters utm;

    // Client-side timestamp (server will also record its own)
    private Instant clientTimestamp;

    // Flexible catch-all for extra context (e.g. {"referrer": "...", "device": "mobile"})
    private Map<String, String> metadata;

    public BehaviorEventDTO() {}

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

    public UtmParameters getUtm() { return utm; }
    public void setUtm(UtmParameters utm) { this.utm = utm; }

    public Instant getClientTimestamp() { return clientTimestamp; }
    public void setClientTimestamp(Instant clientTimestamp) { this.clientTimestamp = clientTimestamp; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
}
