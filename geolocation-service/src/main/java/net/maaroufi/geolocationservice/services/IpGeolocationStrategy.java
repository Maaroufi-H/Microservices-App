package net.maaroufi.geolocationservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.maaroufi.geolocationservice.dto.GeolocationRequest;
import net.maaroufi.geolocationservice.entities.UserGeolocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * IP-based geolocation using ip-api.com.
 * Free tier: 45 req/min, no API key required.
 * URL: http://ip-api.com/json/{ip}?fields=status,city,regionName,country,countryCode,lat,lon,timezone
 */
@Component
public class IpGeolocationStrategy implements GeocodingStrategy {

    private static final Logger log = LoggerFactory.getLogger(IpGeolocationStrategy.class);
    private static final String IP_API_URL = "http://ip-api.com/json/";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public IpGeolocationStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public boolean supports(GeolocationRequest request) {
        return request.getIpAddress() != null && !request.getIpAddress().isBlank();
    }

    @Override
    public void enrich(UserGeolocation geo, GeolocationRequest request) {
        try {
            enrichFromIp(geo, request.getIpAddress());
        } catch (Exception e) {
            log.warn("IP geolocation failed for ip={}: {}", request.getIpAddress(), e.getMessage());
        }
        geo.setSource("IP");
        geo.setAccuracyMeters(null); // IP geolocation is city-level, accuracy not applicable
    }

    @Cacheable(value = "ip-geocode", key = "#ip")
    public String fetchIpJson(String ip) {
        return restClient.get()
                .uri(IP_API_URL + ip + "?fields=status,city,regionName,country,countryCode,lat,lon,timezone")
                .retrieve()
                .body(String.class);
    }

    private void enrichFromIp(UserGeolocation geo, String ip) throws Exception {
        String json = fetchIpJson(ip);
        if (json == null) return;

        JsonNode root = objectMapper.readTree(json);
        if (!"success".equals(root.path("status").asText(null))) {
            log.warn("ip-api.com returned non-success status for ip={}", ip);
            return;
        }

        geo.setCity(root.path("city").asText(null));
        geo.setState(root.path("regionName").asText(null));
        geo.setCountry(root.path("country").asText(null));
        geo.setCountryCode(root.path("countryCode").asText(null));
        geo.setTimezone(root.path("timezone").asText(null));

        double lat = root.path("lat").asDouble(0);
        double lon = root.path("lon").asDouble(0);
        if (lat != 0 || lon != 0) {
            geo.setLatitude(lat);
            geo.setLongitude(lon);
        }
    }
}
