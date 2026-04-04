package net.maaroufi.recommendationservice.dto;

import java.util.List;

/**
 * Maps the JSON response from the Azure ML real-time endpoint.
 *
 * Expected format:
 * {
 *   "customer_id": 123,
 *   "recommendations": [
 *     { "product_id": 45, "score": 0.95 },
 *     { "product_id": 12, "score": 0.87 }
 *   ]
 * }
 */
public class AzureMLResponse {

    private Long customer_id;
    private List<ScoredProduct> recommendations;

    public Long getCustomer_id() { return customer_id; }
    public void setCustomer_id(Long customer_id) { this.customer_id = customer_id; }

    public List<ScoredProduct> getRecommendations() { return recommendations; }
    public void setRecommendations(List<ScoredProduct> recommendations) {
        this.recommendations = recommendations;
    }

    public static class ScoredProduct {
        private Long product_id;
        private Double score;

        public Long getProduct_id() { return product_id; }
        public void setProduct_id(Long product_id) { this.product_id = product_id; }

        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
    }
}
