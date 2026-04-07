package net.maaroufi.geolocationservice.services;

import net.maaroufi.geolocationservice.dto.GeolocationRequest;
import net.maaroufi.geolocationservice.entities.UserGeolocation;

public interface GeocodingStrategy {

    /**
     * Enriches the UserGeolocation entity with geocoding data
     * (city, country, timezone, etc.) from this strategy's source.
     */
    void enrich(UserGeolocation geo, GeolocationRequest request);

    /**
     * Returns true if this strategy can handle the given request.
     */
    boolean supports(GeolocationRequest request);
}
