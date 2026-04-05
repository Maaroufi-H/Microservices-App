package net.maaroufi.geolocationservice.dto;

/**
 * Request payload for POST /api/geolocation/locate.
 *
 * The browser Geolocation API provides latitude and longitude after the user
 * accepts the popup. The frontend sends this payload to the service.
 *
 * Example payload:
 * {
 *   "customerId": 42,
 *   "sessionId": "sess-abc",
 *   "latitude": 48.8566,
 *   "longitude": 2.3522
 * }
 */
public class GeolocationRequest {

    /** Null for anonymous users. */
    private Long customerId;

    private String sessionId;

    /** Latitude from navigator.geolocation.getCurrentPosition() */
    private Double latitude;

    /** Longitude from navigator.geolocation.getCurrentPosition() */
    private Double longitude;

    public GeolocationRequest() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
