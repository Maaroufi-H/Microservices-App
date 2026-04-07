package net.maaroufi.geolocationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.maaroufi.geolocationservice.controllers.GeolocationController;
import net.maaroufi.geolocationservice.dto.DistanceResult;
import net.maaroufi.geolocationservice.dto.GeolocationRequest;
import net.maaroufi.geolocationservice.dto.GeolocationResponse;
import net.maaroufi.geolocationservice.services.GeolocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GeolocationController.class)
class GeolocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GeolocationService geolocationService;

    @Test
    void locate_withValidCoords_returns200() throws Exception {
        GeolocationRequest req = new GeolocationRequest();
        req.setLatitude(48.8566);
        req.setLongitude(2.3522);

        GeolocationResponse resp = new GeolocationResponse();
        resp.setGeolocationId(1L);
        resp.setCity("Paris");
        resp.setCountry("France");
        resp.setCountryCode("fr");
        resp.setTimezone("Europe/Paris");
        resp.setSource("GPS");
        resp.setLatitude(48.8566);
        resp.setLongitude(2.3522);
        resp.setLocatedAt(Instant.now());

        when(geolocationService.locate(any())).thenReturn(resp);

        mockMvc.perform(post("/api/geolocation/locate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Paris"))
                .andExpect(jsonPath("$.country").value("France"))
                .andExpect(jsonPath("$.source").value("GPS"));
    }

    @Test
    void locate_withIllegalArgument_returns400() throws Exception {
        GeolocationRequest req = new GeolocationRequest();
        when(geolocationService.locate(any()))
                .thenThrow(new IllegalArgumentException("Either latitude/longitude or ipAddress must be provided"));

        mockMvc.perform(post("/api/geolocation/locate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void distance_Paris_to_London_returns200() throws Exception {
        DistanceResult result = new DistanceResult(48.8566, 2.3522, 51.5074, -0.1278, 341.57, 212.24);
        when(geolocationService.calculateDistance(48.8566, 2.3522, 51.5074, -0.1278)).thenReturn(result);

        mockMvc.perform(get("/api/geolocation/distance")
                        .param("lat1", "48.8566").param("lng1", "2.3522")
                        .param("lat2", "51.5074").param("lng2", "-0.1278"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distanceKm", greaterThan(300.0)))
                .andExpect(jsonPath("$.distanceMiles", greaterThan(180.0)));
    }

    @Test
    void health_returns200() throws Exception {
        mockMvc.perform(get("/api/geolocation/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void countryStats_returnsEmptyList() throws Exception {
        when(geolocationService.getCountryStats()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/geolocation/stats/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
