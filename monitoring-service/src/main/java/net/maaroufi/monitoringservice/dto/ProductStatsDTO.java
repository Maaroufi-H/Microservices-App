package net.maaroufi.monitoringservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

/**
 * Mirror DTO of ProductTrackingStats from tracking-service.
 * Used by the Feign client to deserialize the response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductStatsDTO {

    private Long productId;
    private long viewCount;
    private long totalViewDurationMs;
    private double averageViewDurationMs;
    private Instant lastViewedAt;
    private String domainNamespace;

    public ProductStatsDTO() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public long getViewCount() { return viewCount; }
    public void setViewCount(long viewCount) { this.viewCount = viewCount; }

    public long getTotalViewDurationMs() { return totalViewDurationMs; }
    public void setTotalViewDurationMs(long totalViewDurationMs) { this.totalViewDurationMs = totalViewDurationMs; }

    public double getAverageViewDurationMs() { return averageViewDurationMs; }
    public void setAverageViewDurationMs(double averageViewDurationMs) { this.averageViewDurationMs = averageViewDurationMs; }

    public Instant getLastViewedAt() { return lastViewedAt; }
    public void setLastViewedAt(Instant lastViewedAt) { this.lastViewedAt = lastViewedAt; }

    public String getDomainNamespace() { return domainNamespace; }
    public void setDomainNamespace(String domainNamespace) { this.domainNamespace = domainNamespace; }
}
