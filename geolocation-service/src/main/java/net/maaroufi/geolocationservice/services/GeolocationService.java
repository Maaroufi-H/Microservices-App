package net.maaroufi.geolocationservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.maaroufi.geolocationservice.dto.*;
import net.maaroufi.geolocationservice.entities.UserGeolocation;
import net.maaroufi.geolocationservice.repository.UserGeolocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeolocationService {

    private static final Logger log = LoggerFactory.getLogger(GeolocationService.class);
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final UserGeolocationRepository repository;
    private final NominatimStrategy nominatimStrategy;
    private final IpGeolocationStrategy ipStrategy;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    @Value("${geolocation.nominatim.forward-url:https://nominatim.openstreetmap.org/search}")
    private String nominatimForwardUrl;

    public GeolocationService(UserGeolocationRepository repository,
                              NominatimStrategy nominatimStrategy,
                              IpGeolocationStrategy ipStrategy,
                              ObjectMapper objectMapper,
                              @Value("${geolocation.nominatim.user-agent:microservices-app/1.0}") String userAgent) {
        this.repository = repository;
        this.nominatimStrategy = nominatimStrategy;
        this.ipStrategy = ipStrategy;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * GPS-based reverse geocoding via Nominatim.
     * Falls back to IP-based if lat/lng are absent but ipAddress is provided.
     */
    public GeolocationResponse locate(GeolocationRequest request) {
        if (nominatimStrategy.supports(request)) {
            double lat = request.getLatitude();
            double lng = request.getLongitude();
            if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
                throw new IllegalArgumentException(
                        "latitude must be in [-90, 90] and longitude in [-180, 180]");
            }
            UserGeolocation geo = buildBase(request);
            nominatimStrategy.enrich(geo, request);
            return toResponse(repository.save(geo));
        }

        if (ipStrategy.supports(request)) {
            return locateByIp(request);
        }

        throw new IllegalArgumentException(
                "Either latitude/longitude or ipAddress must be provided");
    }

    /**
     * IP-based geolocation via ip-api.com.
     */
    public GeolocationResponse locateByIp(GeolocationRequest request) {
        if (!ipStrategy.supports(request)) {
            throw new IllegalArgumentException("ipAddress is required");
        }
        UserGeolocation geo = buildBase(request);
        ipStrategy.enrich(geo, request);
        return toResponse(repository.save(geo));
    }

    /**
     * Forward geocoding: text query → list of candidate coordinates.
     * Results are cached for 24h per query string.
     */
    @Cacheable(value = "forward-geocode", key = "#query.toLowerCase().trim()")
    public List<ForwardGeocodeResult> forwardGeocode(String query, int limit) {
        String url = UriComponentsBuilder.fromHttpUrl(nominatimForwardUrl)
                .queryParam("format", "json")
                .queryParam("q", query)
                .queryParam("limit", limit)
                .toUriString();

        String json = restClient.get().uri(url).retrieve().body(String.class);
        List<ForwardGeocodeResult> results = new ArrayList<>();
        if (json == null) return results;

        try {
            JsonNode arr = objectMapper.readTree(json);
            for (JsonNode node : arr) {
                ForwardGeocodeResult r = new ForwardGeocodeResult();
                r.setDisplayName(node.path("display_name").asText(null));
                r.setLat(parseDouble(node.path("lat").asText(null)));
                r.setLon(parseDouble(node.path("lon").asText(null)));
                r.setType(node.path("type").asText(null));
                r.setImportance(node.path("importance").isNull() ? null
                        : node.path("importance").asDouble());
                results.add(r);
            }
        } catch (Exception e) {
            log.warn("Forward geocode parse error for query={}: {}", query, e.getMessage());
        }
        return results;
    }

    /**
     * Haversine distance calculation between two geographic points.
     */
    public DistanceResult calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double distanceKm = EARTH_RADIUS_KM * c;
        double distanceMiles = distanceKm * 0.621371;
        return new DistanceResult(lat1, lng1, lat2, lng2,
                Math.round(distanceKm * 100.0) / 100.0,
                Math.round(distanceMiles * 100.0) / 100.0);
    }

    public List<CountryStatsDTO> getCountryStats() {
        return repository.findCountryStats();
    }

    public List<UserGeolocation> getHistory(Long customerId) {
        return repository.findByCustomerIdOrderByLocatedAtDesc(customerId);
    }

    public UserGeolocation getLatest(Long customerId) {
        return repository.findTopByCustomerIdOrderByLocatedAtDesc(customerId).orElse(null);
    }

    // ---- Helpers ----

    private UserGeolocation buildBase(GeolocationRequest request) {
        UserGeolocation geo = new UserGeolocation();
        geo.setCustomerId(request.getCustomerId());
        geo.setSessionId(request.getSessionId());
        geo.setLatitude(request.getLatitude());
        geo.setLongitude(request.getLongitude());
        geo.setLocatedAt(Instant.now());
        return geo;
    }

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
        r.setAccuracyMeters(geo.getAccuracyMeters());
        r.setTimezone(geo.getTimezone());
        r.setSource(geo.getSource());
        return r;
    }

    private Double parseDouble(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return null; }
    }
}
