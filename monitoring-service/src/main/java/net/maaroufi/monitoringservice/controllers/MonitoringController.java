package net.maaroufi.monitoringservice.controllers;

import net.maaroufi.monitoringservice.dto.DashboardDTO;
import net.maaroufi.monitoringservice.dto.ProductStatsDTO;
import net.maaroufi.monitoringservice.dto.ServiceStatusDTO;
import net.maaroufi.monitoringservice.services.MonitoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@CrossOrigin(origins = "*")
public class MonitoringController {

    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    /**
     * Full monitoring dashboard.
     *
     * Returns:
     *   - topProducts : most viewed products (ordered by viewCount desc)
     *   - serviceStatuses : health of all known microservices
     *   - totalProductViews : sum of all product views across the platform
     *   - generatedAt : timestamp of this response
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard(
            @RequestParam(defaultValue = "10") int topN) {
        return ResponseEntity.ok(monitoringService.getDashboard(topN));
    }

    /**
     * Top N most viewed products.
     * Query param: limit (default 10)
     */
    @GetMapping("/products/top")
    public ResponseEntity<List<ProductStatsDTO>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(monitoringService.getTopProducts(limit));
    }

    /**
     * Health status of all known microservices.
     * Polls each service's /actuator/health endpoint.
     */
    @GetMapping("/services/status")
    public ResponseEntity<List<ServiceStatusDTO>> getServiceStatuses() {
        return ResponseEntity.ok(monitoringService.getServiceStatuses());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("service", "monitoring-service", "status", "UP"));
    }
}
