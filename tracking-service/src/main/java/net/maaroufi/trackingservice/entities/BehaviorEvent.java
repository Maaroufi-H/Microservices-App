package net.maaroufi.trackingservice.entities;

import jakarta.persistence.*;
import net.maaroufi.trackingservice.enums.EventType;

import java.time.Instant;

@Entity
@Table(name = "behavior_events")
public class BehaviorEvent {

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

    // UTM parameters (from Facebook ads)
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

    public BehaviorEvent() {}

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
}
