package net.maaroufi.recommendationservice.feign;

import net.maaroufi.recommendationservice.dto.ProductRecommendation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to fetch product details from product-service.
 * Used to enrich ML recommendations with product metadata.
 */
@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductRecommendation getProductById(@PathVariable Long id);

    @GetMapping("/api/products")
    PagedModel<ProductRecommendation> getAllProducts();
}
