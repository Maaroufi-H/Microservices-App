package net.maaroufi.geolocationservice.dto;

import java.time.Instant;

public class GeolocationResponse {

    private Long geolocationId;
    private Long customerId;
    private String sessionId;
    private Double latitude;
    private Double longitude;
    private String city;
    private String state;
    private String country;
    private String countryCode;
    private String postcode;
    private String fullAddress;
    private Instant locatedAt;
    private Double accuracyMeters;
    private String timezone;
    private String source;

    public GeolocationResponse() {}

    public Long getGeolocationId() { return geolocationId; }
    public void setGeolocationId(Long geolocationId) { this.geolocationId = geolocationId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }

    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }

    public Instant getLocatedAt() { return locatedAt; }
    public void setLocatedAt(Instant locatedAt) { this.locatedAt = locatedAt; }

    public Double getAccuracyMeters() { return accuracyMeters; }
    public void setAccuracyMeters(Double accuracyMeters) { this.accuracyMeters = accuracyMeters; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
