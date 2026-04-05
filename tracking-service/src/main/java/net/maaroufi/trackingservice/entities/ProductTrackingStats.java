package net.maaroufi.trackingservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Aggregated tracking statistics per product.
 *
 * One row per productId. Updated (upserted) each time a PRODUCT_VIEW event is ingested.
 *
 * Fields:
 *   - viewCount            : total number of PRODUCT_VIEW events for this product
 *   - totalViewDurationMs  : sum of all viewDurationMs across sessions
 *   - lastViewedAt         : server timestamp of the most recent PRODUCT_VIEW
 *   - domainNamespace      : "ecommerce", "football", etc.
 */
@Entity
@Table(name = "product_tracking_stats")
public class ProductTrackingStats {

    @Id
    private Long productId;

    private long viewCount = 0;
    private long totalViewDurationMs = 0;
    private Instant lastViewedAt;
    private String domainNamespace;

    public ProductTrackingStats() {}

    public ProductTrackingStats(Long productId, String domainNamespace) {
        this.productId = productId;
        this.domainNamespace = domainNamespace;
    }

    // ---- Getters / Setters ----

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public long getViewCount() { return viewCount; }
    public void setViewCount(long viewCount) { this.viewCount = viewCount; }

    public long getTotalViewDurationMs() { return totalViewDurationMs; }
    public void setTotalViewDurationMs(long totalViewDurationMs) { this.totalViewDurationMs = totalViewDurationMs; }

    public Instant getLastViewedAt() { return lastViewedAt; }
    public void setLastViewedAt(Instant lastViewedAt) { this.lastViewedAt = lastViewedAt; }

    public String getDomainNamespace() { return domainNamespace; }
    public void setDomainNamespace(String domainNamespace) { this.domainNamespace = domainNamespace; }

    /** Computed — not persisted. Returns 0.0 if no views yet. */
    public double getAverageViewDurationMs() {
        return viewCount == 0 ? 0.0 : (double) totalViewDurationMs / viewCount;
    }
}
