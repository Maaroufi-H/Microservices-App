package net.maaroufi.recommendationservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.maaroufi.core.domain.AppDomainContext;
import net.maaroufi.core.recommendation.IRecommendationEngine;
import net.maaroufi.recommendationservice.dto.AzureMLResponse;
import net.maaroufi.recommendationservice.dto.ProductRecommendation;
import net.maaroufi.recommendationservice.dto.RecommendationResponse;
import net.maaroufi.recommendationservice.feign.ProductClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService implements IRecommendationEngine {

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

    @Autowired(required = false)
    private AppDomainContext domainContext;

    public RecommendationService(ProductClient productClient) {
        this.productClient = productClient;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Returns personalized recommendations for a customer.
     *
     * Strategy:
     *  1. Try to call Azure ML real-time endpoint (with domain_namespace for multi-domain routing).
     *  2. On failure, fall back to returning the full product catalog ranked by price.
     */
    public RecommendationResponse getRecommendations(Long customerId) {
        String namespace = domainContext != null ? domainContext.getNamespace() : "ecommerce";
        if (!azureMlEndpointUrl.startsWith("https://YOUR_")) {
            try {
                return callAzureML(customerId, namespace);
            } catch (Exception e) {
                System.err.println("[recommendation-service] Azure ML call failed: " + e.getMessage());
            }
        }
        return popularProductFallback(customerId);
    }

    /** IRecommendationEngine contract — delegates to getRecommendations. */
    @Override
    public List<ProductRecommendation> recommend(Long customerId, String domainNamespace, int topN) {
        return callAzureMLRaw(customerId, domainNamespace, topN);
    }

    private RecommendationResponse callAzureML(Long customerId, String domainNamespace) throws Exception {
        List<ProductRecommendation> recommendations = callAzureMLRaw(customerId, domainNamespace, topN);
        return new RecommendationResponse(customerId, "azure-ml", recommendations);
    }

    private List<ProductRecommendation> callAzureMLRaw(Long customerId, String domainNamespace, int n) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + azureMlApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("customer_id", customerId);
        requestBody.put("top_n", n);
        // domain_namespace routes the request to the correct Azure ML model
        requestBody.put("domain_namespace", domainNamespace);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(azureMlEndpointUrl, request, String.class);
            AzureMLResponse mlResponse = objectMapper.readValue(response.getBody(), AzureMLResponse.class);

            return mlResponse.getRecommendations().stream()
                    .map(scored -> {
                        try {
                            ProductRecommendation product = productClient.getProductById(scored.getProduct_id());
                            product.setScore(scored.getScore());
                            return product;
                        } catch (Exception e) {
                            ProductRecommendation stub = new ProductRecommendation();
                            stub.setProductId(scored.getProduct_id());
                            stub.setScore(scored.getScore());
                            return stub;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Cold-start fallback: returns all products sorted by price descending.
     */
    private RecommendationResponse popularProductFallback(Long customerId) {
        try {
            PagedModel<ProductRecommendation> paged = productClient.getAllProducts();
            List<ProductRecommendation> products = new ArrayList<>(paged.getContent());

            products.sort(Comparator.comparingDouble(p -> -(p.getPrice() != null ? p.getPrice() : 0.0)));

            List<ProductRecommendation> top = products.stream()
                    .limit(fallbackTopN)
                    .peek(p -> p.setScore(1.0))
                    .collect(Collectors.toList());

            return new RecommendationResponse(customerId, "popular-fallback", top);
        } catch (Exception e) {
            return new RecommendationResponse(customerId, "popular-fallback", Collections.emptyList());
        }
    }
}
