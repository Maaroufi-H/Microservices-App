package net.maaroufi.geolocationservice.repository;

import net.maaroufi.geolocationservice.entities.UserGeolocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserGeolocationRepository extends JpaRepository<UserGeolocation, Long> {

    /** All geolocations for a customer, most recent first. */
    List<UserGeolocation> findByCustomerIdOrderByLocatedAtDesc(Long customerId);

    /** Most recent geolocation for a customer. */
    Optional<UserGeolocation> findTopByCustomerIdOrderByLocatedAtDesc(Long customerId);

    List<UserGeolocation> findBySessionId(String sessionId);
}
