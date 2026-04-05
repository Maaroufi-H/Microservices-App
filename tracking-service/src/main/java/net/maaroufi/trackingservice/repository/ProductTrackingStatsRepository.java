package net.maaroufi.trackingservice.repository;

import net.maaroufi.trackingservice.entities.ProductTrackingStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductTrackingStatsRepository extends JpaRepository<ProductTrackingStats, Long> {

    /** Returns all product stats ordered by view count descending (most viewed first). */
    List<ProductTrackingStats> findAllByOrderByViewCountDesc();

    List<ProductTrackingStats> findByDomainNamespace(String domainNamespace);
}
