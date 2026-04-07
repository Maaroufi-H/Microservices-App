package net.maaroufi.geolocationservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.maaroufi.geolocationservice.dto.*;
import net.maaroufi.geolocationservice.entities.UserGeolocation;
import net.maaroufi.geolocationservice.services.GeolocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/geolocation")
@CrossOrigin(origins = "*")
@Tag(name = "Geolocation", description = "Multi-source geolocation: GPS reverse-geocoding (Nominatim) + IP fallback (ip-api.com)")
public class GeolocationController {

    private final GeolocationService geolocationService;

    public GeolocationController(GeolocationService geolocationService) {
        this.geolocationService = geolocationService;
    }

    @Operation(summary = "Reverse geocode GPS coordinates",
               description = "Resolves lat/lng to an address via Nominatim. Falls back to IP-based geolocation if lat/lng are absent but ipAddress is provided.")
    @PostMapping("/locate")
    public ResponseEntity<?> locate(@RequestBody GeolocationRequest request) {
        try {
            return ResponseEntity.ok(geolocationService.locate(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Geolocation failed: " + e.getMessage()));
        }
    }

    @Operation(summary = "IP-based geolocation",
               description = "Resolves an IP address to a city/country/timezone via ip-api.com. Free, no API key required.")
    @PostMapping("/locate-by-ip")
    public ResponseEntity<?> locateByIp(@RequestBody GeolocationRequest request) {
        try {
            return ResponseEntity.ok(geolocationService.locateByIp(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "IP geolocation failed: " + e.getMessage()));
        }
    }

    @Operation(summary = "Forward geocoding — text search to coordinates",
               description = "Searches Nominatim for a place name or address and returns matching locations with coordinates.")
    @GetMapping("/search")
    public ResponseEntity<List<ForwardGeocodeResult>> search(
            @Parameter(description = "Place name or address query", example = "Casablanca")
            @RequestParam String q,
            @Parameter(description = "Max results to return (1-20)")
            @RequestParam(defaultValue = "5") int limit) {
        limit = Math.max(1, Math.min(limit, 20));
        return ResponseEntity.ok(geolocationService.forwardGeocode(q, limit));
    }

    @Operation(summary = "Haversine distance between two points",
               description = "Calculates the great-circle distance in km and miles between two geographic coordinates. No external API call.")
    @GetMapping("/distance")
    public ResponseEntity<?> distance(
            @RequestParam double lat1, @RequestParam double lng1,
            @RequestParam double lat2, @RequestParam double lng2) {
        try {
            return ResponseEntity.ok(geolocationService.calculateDistance(lat1, lng1, lat2, lng2));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Country-level statistics",
               description = "Returns the number of geolocation records grouped by country, sorted by count descending.")
    @GetMapping("/stats/countries")
    public ResponseEntity<List<CountryStatsDTO>> countryStats() {
        return ResponseEntity.ok(geolocationService.getCountryStats());
    }

    @Operation(summary = "Customer geolocation history")
    @GetMapping("/history/{customerId}")
    public ResponseEntity<List<UserGeolocation>> getHistory(@PathVariable Long customerId) {
        return ResponseEntity.ok(geolocationService.getHistory(customerId));
    }

    @Operation(summary = "Latest geolocation for a customer")
    @GetMapping("/latest/{customerId}")
    public ResponseEntity<?> getLatest(@PathVariable Long customerId) {
        UserGeolocation latest = geolocationService.getLatest(customerId);
        if (latest == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latest);
    }

    @Operation(summary = "Health check")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("service", "geolocation-service", "status", "UP"));
    }
}
