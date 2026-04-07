package net.maaroufi.geolocationservice.repository;

import net.maaroufi.geolocationservice.dto.CountryStatsDTO;
import net.maaroufi.geolocationservice.entities.UserGeolocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserGeolocationRepository extends JpaRepository<UserGeolocation, Long> {

    /** All geolocations for a customer, most recent first. */
    List<UserGeolocation> findByCustomerIdOrderByLocatedAtDesc(Long customerId);

    /** Most recent geolocation for a customer. */
    Optional<UserGeolocation> findTopByCustomerIdOrderByLocatedAtDesc(Long customerId);

    List<UserGeolocation> findBySessionId(String sessionId);

    /** Country-level aggregation: country, countryCode, count of records. */
    @Query("SELECT new net.maaroufi.geolocationservice.dto.CountryStatsDTO(g.country, g.countryCode, COUNT(g)) " +
           "FROM UserGeolocation g WHERE g.country IS NOT NULL " +
           "GROUP BY g.country, g.countryCode ORDER BY COUNT(g) DESC")
    List<CountryStatsDTO> findCountryStats();
}
