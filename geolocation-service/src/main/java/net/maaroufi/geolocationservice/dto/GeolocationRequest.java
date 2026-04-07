package net.maaroufi.geolocationservice.dto;

public class GeolocationRequest {

    /** Null for anonymous users. */
    private Long customerId;

    private String sessionId;

    /** Latitude from navigator.geolocation.getCurrentPosition() */
    private Double latitude;

    /** Longitude from navigator.geolocation.getCurrentPosition() */
    private Double longitude;

    /** GPS accuracy in meters from navigator.geolocation.getCurrentPosition(). */
    private Double accuracyMeters;

    /** Client IP address for IP-based geolocation fallback. */
    private String ipAddress;

    public GeolocationRequest() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getAccuracyMeters() { return accuracyMeters; }
    public void setAccuracyMeters(Double accuracyMeters) { this.accuracyMeters = accuracyMeters; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
