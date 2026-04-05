package net.maaroufi.monitoringservice.services;

import net.maaroufi.monitoringservice.dto.DashboardDTO;
import net.maaroufi.monitoringservice.dto.ProductStatsDTO;
import net.maaroufi.monitoringservice.dto.ServiceStatusDTO;
import net.maaroufi.monitoringservice.feign.TrackingServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MonitoringService {

    private final TrackingServiceClient trackingClient;
    private final RestTemplate restTemplate;

    // Known services with their actuator health endpoints
    private static final Map<String, String> SERVICE_HEALTH_URLS = new LinkedHashMap<>();

    static {
        SERVICE_HEALTH_URLS.put("customer-service",        "http://localhost:8082/actuator/health");
        SERVICE_HEALTH_URLS.put("product-service",         "http://localhost:8081/actuator/health");
        SERVICE_HEALTH_URLS.put("order-service",           "http://localhost:8084/actuator/health");
        SERVICE_HEALTH_URLS.put("payment-service",         "http://localhost:8083/actuator/health");
        SERVICE_HEALTH_URLS.put("tracking-service",        "http://localhost:8085/actuator/health");
        SERVICE_HEALTH_URLS.put("recommendation-service",  "http://localhost:8086/actuator/health");
        SERVICE_HEALTH_URLS.put("geolocation-service",     "http://localhost:8090/actuator/health");
    }

    private static final Map<String, String> SERVICE_PORTS;
    static {
        SERVICE_PORTS = new LinkedHashMap<>();
        SERVICE_PORTS.put("customer-service",       "8082");
        SERVICE_PORTS.put("product-service",        "8081");
        SERVICE_PORTS.put("order-service",          "8084");
        SERVICE_PORTS.put("payment-service",        "8083");
        SERVICE_PORTS.put("tracking-service",       "8085");
        SERVICE_PORTS.put("recommendation-service", "8086");
        SERVICE_PORTS.put("geolocation-service",    "8090");
    }

    public MonitoringService(TrackingServiceClient trackingClient) {
        this.trackingClient = trackingClient;
        this.restTemplate = new RestTemplate();
    }

    public DashboardDTO getDashboard(int topN) {
        List<ProductStatsDTO> topProducts = getTopProducts(topN);
        List<ServiceStatusDTO> statuses = getServiceStatuses();
        return new DashboardDTO(topProducts, statuses);
    }

    public List<ProductStatsDTO> getTopProducts(int limit) {
        try {
            List<ProductStatsDTO> all = trackingClient.getAllProductStats();
            return all.stream().limit(limit).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<ServiceStatusDTO> getServiceStatuses() {
        return SERVICE_HEALTH_URLS.entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    String url  = entry.getValue();
                    String port = SERVICE_PORTS.getOrDefault(name, "?");
                    try {
                        Map<?, ?> response = restTemplate.getForObject(url, Map.class);
                        String status = response != null ? (String) response.get("status") : "UNKNOWN";
                        return new ServiceStatusDTO(name, status != null ? status : "UNKNOWN", port);
                    } catch (Exception e) {
                        return new ServiceStatusDTO(name, "DOWN", port);
                    }
                })
                .collect(Collectors.toList());
    }
}
