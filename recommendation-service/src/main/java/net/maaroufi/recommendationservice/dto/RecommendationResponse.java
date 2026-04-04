package net.maaroufi.recommendationservice.dto;

import java.util.List;

public class RecommendationResponse {

    private Long customerId;
    private String source; // "azure-ml" or "popular-fallback"
    private List<ProductRecommendation> recommendations;

    public RecommendationResponse() {}

    public RecommendationResponse(Long customerId, String source,
                                   List<ProductRecommendation> recommendations) {
        this.customerId = customerId;
        this.source = source;
        this.recommendations = recommendations;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public List<ProductRecommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<ProductRecommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
