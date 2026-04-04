package net.maaroufi.recommendationservice.dto;

public class ProductRecommendation {

    private Long productId;
    private String name;
    private String description;
    private Double price;
    private String category;
    private String tags;
    // Confidence score from the ML model (0.0 – 1.0)
    private Double score;

    public ProductRecommendation() {}

    public ProductRecommendation(Long productId, String name, String description,
                                  Double price, String category, String tags, Double score) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.tags = tags;
        this.score = score;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
}
