package net.maaroufi.geolocationservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.maaroufi.geolocationservice.dto.GeolocationRequest;
import net.maaroufi.geolocationservice.entities.UserGeolocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZoneId;

@Component
@Primary
public class NominatimStrategy implements GeocodingStrategy {

    private static final Logger log = LoggerFactory.getLogger(NominatimStrategy.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${geolocation.nominatim.url:https://nominatim.openstreetmap.org/reverse}")
    private String nominatimUrl;

    public NominatimStrategy(ObjectMapper objectMapper,
                             @Value("${geolocation.nominatim.user-agent:microservices-app/1.0}") String userAgent) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public boolean supports(GeolocationRequest request) {
        return request.getLatitude() != null && request.getLongitude() != null;
    }

    @Override
    public void enrich(UserGeolocation geo, GeolocationRequest request) {
        try {
            String cacheKey = roundedKey(request.getLatitude(), request.getLongitude());
            enrichCached(geo, request.getLatitude(), request.getLongitude(), cacheKey);
        } catch (Exception e) {
            log.warn("Nominatim enrichment failed for lat={} lng={}: {}",
                    request.getLatitude(), request.getLongitude(), e.getMessage());
        }
        geo.setSource("GPS");
        geo.setAccuracyMeters(request.getAccuracyMeters());
    }

    @Cacheable(value = "reverse-geocode", key = "#cacheKey")
    public String fetchNominatimJson(double lat, double lng, String cacheKey) {
        String url = UriComponentsBuilder.fromHttpUrl(nominatimUrl)
                .queryParam("format", "json")
                .queryParam("lat", lat)
                .queryParam("lon", lng)
                .queryParam("addressdetails", 1)
                .toUriString();

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
    }

    private void enrichCached(UserGeolocation geo, double lat, double lng, String cacheKey) throws Exception {
        String json = fetchNominatimJson(lat, lng, cacheKey);
        if (json == null) return;

        JsonNode root = objectMapper.readTree(json);
        geo.setFullAddress(root.path("display_name").asText(null));

        JsonNode address = root.path("address");
        if (!address.isMissingNode()) {
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

            // Derive timezone from country code using Java's ZoneId
            String cc = address.path("country_code").asText(null);
            if (cc != null) {
                String tz = timezoneFromCountryCode(cc.toUpperCase(), lat, lng);
                geo.setTimezone(tz);
            }
        }
    }

    /**
     * Best-effort timezone resolution.
     * For countries with a single timezone the ZoneId match is exact.
     * For multi-tz countries (US, CA, RU…) we fall back to the UTC offset
     * estimated from the longitude (15° per hour).
     */
    private String timezoneFromCountryCode(String cc, double lat, double lng) {
        // Try to find a JVM zone that contains the country code
        for (String zoneId : ZoneId.getAvailableZoneIds()) {
            if (zoneId.contains("/" ) && zoneId.toUpperCase().endsWith("/" + cc)) {
                return zoneId;
            }
        }
        // Longitude-based UTC offset fallback (rough estimate)
        int offsetHours = (int) Math.round(lng / 15.0);
        if (offsetHours == 0) return "UTC";
        return String.format("Etc/GMT%+d", -offsetHours);
    }

    private static String roundedKey(double lat, double lng) {
        return Math.round(lat * 1000) / 1000.0 + "," + Math.round(lng * 1000) / 1000.0;
    }

    private String firstNonEmpty(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
