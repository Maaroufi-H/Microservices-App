package net.maaroufi.geolocationservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.maaroufi.geolocationservice.dto.GeolocationRequest;
import net.maaroufi.geolocationservice.dto.GeolocationResponse;
import net.maaroufi.geolocationservice.entities.UserGeolocation;
import net.maaroufi.geolocationservice.repository.UserGeolocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.List;

/**
 * Reverse geocoding service using Nominatim (OpenStreetMap).
 *
 * Nominatim API:
 *   GET https://nominatim.openstreetmap.org/reverse?format=json&lat={lat}&lon={lng}
 *   Required header: User-Agent (OSM usage policy)
 *
 * Rate limit: max 1 request/second per the OSM usage policy.
 * For production, consider caching results or using a self-hosted Nominatim instance.
 */
@Service
public class GeolocationService {

    private static final Logger log = LoggerFactory.getLogger(GeolocationService.class);

    private final UserGeolocationRepository repository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${geolocation.nominatim.url:https://nominatim.openstreetmap.org/reverse}")
    private String nominatimUrl;

    @Value("${geolocation.nominatim.user-agent:microservices-app/1.0}")
    private String userAgent;

    public GeolocationService(UserGeolocationRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Main entry point: reverse-geocodes the provided lat/lng via Nominatim,
     * persists the result, and returns a GeolocationResponse.
     *
     * @throws IllegalArgumentException if latitude or longitude is null/invalid
     */
    public GeolocationResponse locate(GeolocationRequest request) {
        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new IllegalArgumentException("latitude and longitude are required");
        }
        if (request.getLatitude() < -90 || request.getLatitude() > 90
                || request.getLongitude() < -180 || request.getLongitude() > 180) {
            throw new IllegalArgumentException("latitude must be in [-90, 90] and longitude in [-180, 180]");
        }

        UserGeolocation geo = new UserGeolocation();
        geo.setCustomerId(request.getCustomerId());
        geo.setSessionId(request.getSessionId());
        geo.setLatitude(request.getLatitude());
        geo.setLongitude(request.getLongitude());
        geo.setLocatedAt(Instant.now());

        // Call Nominatim for reverse geocoding
        try {
            enrichWithNominatim(geo, request.getLatitude(), request.getLongitude());
        } catch (Exception e) {
            log.warn("Nominatim call failed for lat={} lng={}: {}",
                    request.getLatitude(), request.getLongitude(), e.getMessage());
            // Persist coordinates only — geocoding fields will be null
        }

        UserGeolocation saved = repository.save(geo);
        return toResponse(saved);
    }

    /**
     * Calls the Nominatim reverse geocoding API and enriches the UserGeolocation entity.
     *
     * Nominatim JSON structure (relevant fields):
     * {
     *   "display_name": "Eiffel Tower, Paris, ...",
     *   "address": {
     *     "city": "Paris",
     *     "state": "Île-de-France",
     *     "country": "France",
     *     "country_code": "fr",
     *     "postcode": "75007"
     *   }
     * }
     */
    private void enrichWithNominatim(UserGeolocation geo, double lat, double lng) throws Exception {
        String url = UriComponentsBuilder.fromHttpUrl(nominatimUrl)
                .queryParam("format", "json")
                .queryParam("lat", lat)
                .queryParam("lon", lng)
                .queryParam("addressdetails", 1)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        // User-Agent is required by the OpenStreetMap Nominatim usage policy
        headers.set(HttpHeaders.USER_AGENT, userAgent);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());

            geo.setFullAddress(root.path("display_name").asText(null));

            JsonNode address = root.path("address");
            if (!address.isMissingNode()) {
                // Prefer city, fall back to town → village → municipality → county
                String city = firstNonEmpty(
                        address.path("city").asText(null),
                        address.path("town").asText(null),
                        address.path("village").asText(null),
                        address.path("municipality").asText(null),
                        address.path("county").asText(null)
                );
                geo.setCity(city);
                geo.setState(address.path("state").asText(null));
                geo.setCountry(address.path("country").asText(null));
                geo.setCountryCode(address.path("country_code").asText(null));
                geo.setPostcode(address.path("postcode").asText(null));
            }
        }
    }

    public List<UserGeolocation> getHistory(Long customerId) {
        return repository.findByCustomerIdOrderByLocatedAtDesc(customerId);
    }

    public UserGeolocation getLatest(Long customerId) {
        return repository.findTopByCustomerIdOrderByLocatedAtDesc(customerId).orElse(null);
    }

    // ---- Helpers ----

    private GeolocationResponse toResponse(UserGeolocation geo) {
        GeolocationResponse r = new GeolocationResponse();
        r.setGeolocationId(geo.getId());
        r.setCustomerId(geo.getCustomerId());
        r.setSessionId(geo.getSessionId());
        r.setLatitude(geo.getLatitude());
        r.setLongitude(geo.getLongitude());
        r.setCity(geo.getCity());
        r.setState(geo.getState());
        r.setCountry(geo.getCountry());
        r.setCountryCode(geo.getCountryCode());
        r.setPostcode(geo.getPostcode());
        r.setFullAddress(geo.getFullAddress());
        r.setLocatedAt(geo.getLocatedAt());
        return r;
    }

    private String firstNonEmpty(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
