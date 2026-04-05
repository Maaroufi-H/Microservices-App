package net.maaroufi.geolocationservice.entities;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * Persisted result of a user geolocation request.
 *
 * Populated via reverse geocoding (Nominatim) from browser-provided lat/lng.
 * One row per locate request — a user can have multiple entries over time.
 */
@Entity
@Table(name = "user_geolocations")
public class UserGeolocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Null for anonymous users. */
    private Long customerId;

    private String sessionId;

    /** Latitude from the browser Geolocation API. */
    private Double latitude;

    /** Longitude from the browser Geolocation API. */
    private Double longitude;

    /** City name (or town/village if city is absent in Nominatim response). */
    private String city;

    /** Full country name (e.g. "France"). */
    private String country;

    /** ISO 3166-1 alpha-2 country code (e.g. "fr", "ma", "us"). */
    private String countryCode;

    /** Full formatted address returned by Nominatim (display_name). */
    @Column(columnDefinition = "TEXT")
    private String fullAddress;

    /** State / region (e.g. "Île-de-France"). */
    private String state;

    /** Postcode. */
    private String postcode;

    private Instant locatedAt;

    public UserGeolocation() {}

    // ---- Getters / Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }

    public Instant getLocatedAt() { return locatedAt; }
    public void setLocatedAt(Instant locatedAt) { this.locatedAt = locatedAt; }
}
