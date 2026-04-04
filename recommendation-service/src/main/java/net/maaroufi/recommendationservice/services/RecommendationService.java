package net.maaroufi.recommendationservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.maaroufi.recommendationservice.dto.AzureMLResponse;
import net.maaroufi.recommendationservice.dto.ProductRecommendation;
import net.maaroufi.recommendationservice.dto.RecommendationResponse;
import net.maaroufi.recommendationservice.feign.ProductClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final ProductClient productClient;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${azure.ml.endpoint.url}")
    private String azureMlEndpointUrl;

    @Value("${azure.ml.endpoint.api-key}")
    private String azureMlApiKey;

    @Value("${azure.ml.recommendations.top-n:10}")
    private int topN;

    @Value("${app.recommendations.fallback.top-n:10}")
    private int fallbackTopN;

    public RecommendationService(ProductClient productClient) {
        this.productClient = productClient;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Returns personalized recommendations for a customer.
     *
     * Strategy:
     *  1. Try to call Azure ML real-time endpoint.
     *  2. On failure (endpoint not configured, network error, etc.),
     *     fall back to returning the full product catalog ranked by price
     *     (proxy for popularity until real purchase data is available).
     */
    public RecommendationResponse getRecommendations(Long customerId) {
        // Attempt Azure ML if the endpoint is configured
        if (!azureMlEndpointUrl.startsWith("https://YOUR_")) {
            try {
                return callAzureML(customerId);
            } catch (Exception e) {
                // Log and fall through to fallback
                System.err.println("[recommendation-service] Azure ML call failed: " + e.getMessage());
            }
        }

        return popularProductFallback(customerId);
    }

    private RecommendationResponse callAzureML(Long customerId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + azureMlApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("customer_id", customerId);
        requestBody.put("top_n", topN);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(azureMlEndpointUrl, request, String.class);

        AzureMLResponse mlResponse = objectMapper.readValue(response.getBody(), AzureMLResponse.class);

        List<ProductRecommendation> recommendations = mlResponse.getRecommendations().stream()
                .map(scored -> {
                    try {
                        ProductRecommendation product = productClient.getProductById(scored.getProduct_id());
                        product.setScore(scored.getScore());
                        return product;
                    } catch (Exception e) {
                        // Product not found – return a stub with just the score
                        ProductRecommendation stub = new ProductRecommendation();
                        stub.setProductId(scored.getProduct_id());
                        stub.setScore(scored.getScore());
                        return stub;
                    }
                })
                .collect(Collectors.toList());

        return new RecommendationResponse(customerId, "azure-ml", recommendations);
    }

    /**
     * Cold-start fallback: returns all products sorted by price descending.
     * Replace this logic with a real popularity ranking once purchase data accumulates.
     */
    private RecommendationResponse popularProductFallback(Long customerId) {
        try {
            PagedModel<ProductRecommendation> paged = productClient.getAllProducts();
            List<ProductRecommendation> products = new ArrayList<>(paged.getContent());

            // Assign a synthetic score and sort descending by price as a proxy for popularity
            products.sort(Comparator.comparingDouble(p -> -(p.getPrice() != null ? p.getPrice() : 0.0)));

            List<ProductRecommendation> top = products.stream()
                    .limit(fallbackTopN)
                    .peek(p -> p.setScore(1.0)) // uniform score for fallback
                    .collect(Collectors.toList());

            return new RecommendationResponse(customerId, "popular-fallback", top);
        } catch (Exception e) {
            return new RecommendationResponse(customerId, "popular-fallback", Collections.emptyList());
        }
    }
}
