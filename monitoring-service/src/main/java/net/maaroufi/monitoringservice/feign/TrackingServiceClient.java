package net.maaroufi.monitoringservice.feign;

import net.maaroufi.monitoringservice.dto.ProductStatsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "TRACKING-SERVICE", path = "/api/tracking")
public interface TrackingServiceClient {

    /** All product stats ordered by view count desc. */
    @GetMapping("/stats/products")
    List<ProductStatsDTO> getAllProductStats();

    /** Stats for a single product. */
    @GetMapping("/stats/product/{productId}")
    ProductStatsDTO getProductStats(@PathVariable("productId") Long productId);
}
