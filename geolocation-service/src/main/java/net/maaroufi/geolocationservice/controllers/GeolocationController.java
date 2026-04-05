package net.maaroufi.geolocationservice.controllers;

import net.maaroufi.geolocationservice.dto.GeolocationRequest;
import net.maaroufi.geolocationservice.dto.GeolocationResponse;
import net.maaroufi.geolocationservice.entities.UserGeolocation;
import net.maaroufi.geolocationservice.services.GeolocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/geolocation")
@CrossOrigin(origins = "*")
public class GeolocationController {

    private final GeolocationService geolocationService;

    public GeolocationController(GeolocationService geolocationService) {
        this.geolocationService = geolocationService;
    }

    /**
     * Main endpoint — triggered when the user accepts the browser geolocation popup.
     *
     * The frontend calls navigator.geolocation.getCurrentPosition(), then sends:
     * POST /api/geolocation/locate
     * {
     *   "customerId": 42,        // null for anonymous users
     *   "sessionId": "sess-abc",
     *   "latitude": 48.8566,
     *   "longitude": 2.3522
     * }
     *
     * The service reverse-geocodes the coordinates via Nominatim (OpenStreetMap),
     * persists the result, and returns the resolved location.
     */
    @PostMapping("/locate")
    public ResponseEntity<?> locate(@RequestBody GeolocationRequest request) {
        try {
            GeolocationResponse response = geolocationService.locate(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Geolocation failed: " + e.getMessage()));
        }
    }

    /**
     * Returns the full geolocation history for a customer (most recent first).
     */
    @GetMapping("/history/{customerId}")
    public ResponseEntity<List<UserGeolocation>> getHistory(@PathVariable Long customerId) {
        return ResponseEntity.ok(geolocationService.getHistory(customerId));
    }

    /**
     * Returns the most recent geolocation for a customer.
     */
    @GetMapping("/latest/{customerId}")
    public ResponseEntity<?> getLatest(@PathVariable Long customerId) {
        UserGeolocation latest = geolocationService.getLatest(customerId);
        if (latest == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latest);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("service", "geolocation-service", "status", "UP"));
    }
}
