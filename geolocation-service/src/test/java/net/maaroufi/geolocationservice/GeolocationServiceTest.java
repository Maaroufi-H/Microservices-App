package net.maaroufi.geolocationservice;

import net.maaroufi.geolocationservice.dto.DistanceResult;
import net.maaroufi.geolocationservice.dto.GeolocationRequest;
import net.maaroufi.geolocationservice.dto.GeolocationResponse;
import net.maaroufi.geolocationservice.entities.UserGeolocation;
import net.maaroufi.geolocationservice.repository.UserGeolocationRepository;
import net.maaroufi.geolocationservice.services.GeolocationService;
import net.maaroufi.geolocationservice.services.IpGeolocationStrategy;
import net.maaroufi.geolocationservice.services.NominatimStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeolocationServiceTest {

    @Mock
    private UserGeolocationRepository repository;
    @Mock
    private NominatimStrategy nominatimStrategy;
    @Mock
    private IpGeolocationStrategy ipStrategy;

    private GeolocationService service;

    @BeforeEach
    void setUp() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        service = new GeolocationService(repository, nominatimStrategy, ipStrategy, mapper,
                "test-agent/1.0");
    }

    @Test
    void testLocate_withValidCoords_returnsResponse() {
        GeolocationRequest req = new GeolocationRequest();
        req.setLatitude(48.8566);
        req.setLongitude(2.3522);

        when(nominatimStrategy.supports(req)).thenReturn(true);
        doAnswer(inv -> {
            UserGeolocation geo = inv.getArgument(0);
            geo.setCity("Paris");
            geo.setCountry("France");
            geo.setCountryCode("fr");
            geo.setTimezone("Europe/Paris");
            geo.setSource("GPS");
            return null;
        }).when(nominatimStrategy).enrich(any(), eq(req));

        UserGeolocation saved = new UserGeolocation();
        saved.setId(1L);
        saved.setCity("Paris");
        saved.setCountry("France");
        saved.setCountryCode("fr");
        saved.setTimezone("Europe/Paris");
        saved.setSource("GPS");
        saved.setLatitude(48.8566);
        saved.setLongitude(2.3522);
        saved.setLocatedAt(Instant.now());
        when(repository.save(any())).thenReturn(saved);

        GeolocationResponse response = service.locate(req);

        assertThat(response.getCity()).isEqualTo("Paris");
        assertThat(response.getCountry()).isEqualTo("France");
        assertThat(response.getSource()).isEqualTo("GPS");
        verify(nominatimStrategy).enrich(any(), eq(req));
        verify(repository).save(any());
    }

    @Test
    void testLocate_withNullCoords_andNoIp_throwsIllegalArgument() {
        GeolocationRequest req = new GeolocationRequest();
        when(nominatimStrategy.supports(req)).thenReturn(false);
        when(ipStrategy.supports(req)).thenReturn(false);

        assertThatThrownBy(() -> service.locate(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("latitude/longitude or ipAddress");
    }

    @Test
    void testLocate_withInvalidCoords_throwsIllegalArgument() {
        GeolocationRequest req = new GeolocationRequest();
        req.setLatitude(200.0);
        req.setLongitude(2.0);
        when(nominatimStrategy.supports(req)).thenReturn(true);

        assertThatThrownBy(() -> service.locate(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("latitude must be in");
    }

    @Test
    void testDistance_Paris_to_London() {
        // Paris: 48.8566, 2.3522 — London: 51.5074, -0.1278
        DistanceResult result = service.calculateDistance(48.8566, 2.3522, 51.5074, -0.1278);

        assertThat(result.getDistanceKm()).isBetween(330.0, 360.0);
        assertThat(result.getDistanceMiles()).isBetween(200.0, 230.0);
    }

    @Test
    void testLocate_NominatimFails_persistsCoordinatesOnly() {
        GeolocationRequest req = new GeolocationRequest();
        req.setLatitude(48.8566);
        req.setLongitude(2.3522);

        when(nominatimStrategy.supports(req)).thenReturn(true);
        doAnswer(inv -> {
            UserGeolocation geo = inv.getArgument(0);
            geo.setSource("GPS");
            return null;
        }).when(nominatimStrategy).enrich(any(), eq(req));

        UserGeolocation saved = new UserGeolocation();
        saved.setId(2L);
        saved.setLatitude(48.8566);
        saved.setLongitude(2.3522);
        saved.setSource("GPS");
        saved.setLocatedAt(Instant.now());
        when(repository.save(any())).thenReturn(saved);

        GeolocationResponse response = service.locate(req);

        assertThat(response.getLatitude()).isEqualTo(48.8566);
        assertThat(response.getLongitude()).isEqualTo(2.3522);
        assertThat(response.getCity()).isNull();
        verify(repository).save(any());
    }
}
