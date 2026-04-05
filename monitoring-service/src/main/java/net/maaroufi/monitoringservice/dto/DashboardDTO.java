package net.maaroufi.monitoringservice.dto;

import java.time.Instant;
import java.util.List;

/**
 * Aggregated monitoring dashboard payload.
 * Returned by GET /api/monitoring/dashboard.
 */
public class DashboardDTO {

    private Instant generatedAt;
    private List<ProductStatsDTO> topProducts;
    private List<ServiceStatusDTO> serviceStatuses;
    private long totalProductViews;

    public DashboardDTO() {}

    public DashboardDTO(List<ProductStatsDTO> topProducts,
                        List<ServiceStatusDTO> serviceStatuses) {
        this.topProducts = topProducts;
        this.serviceStatuses = serviceStatuses;
        this.generatedAt = Instant.now();
        this.totalProductViews = topProducts.stream()
                .mapToLong(ProductStatsDTO::getViewCount)
                .sum();
    }

    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }

    public List<ProductStatsDTO> getTopProducts() { return topProducts; }
    public void setTopProducts(List<ProductStatsDTO> topProducts) { this.topProducts = topProducts; }

    public List<ServiceStatusDTO> getServiceStatuses() { return serviceStatuses; }
    public void setServiceStatuses(List<ServiceStatusDTO> serviceStatuses) { this.serviceStatuses = serviceStatuses; }

    public long getTotalProductViews() { return totalProductViews; }
    public void setTotalProductViews(long totalProductViews) { this.totalProductViews = totalProductViews; }
}
