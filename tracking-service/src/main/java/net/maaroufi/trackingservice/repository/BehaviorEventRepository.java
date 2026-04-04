package net.maaroufi.trackingservice.repository;

import net.maaroufi.trackingservice.entities.BehaviorEvent;
import net.maaroufi.trackingservice.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface BehaviorEventRepository extends JpaRepository<BehaviorEvent, Long> {

    List<BehaviorEvent> findByCustomerId(Long customerId);

    List<BehaviorEvent> findBySessionId(String sessionId);

    List<BehaviorEvent> findByProductIdAndEventType(Long productId, EventType eventType);

    List<BehaviorEvent> findByEventType(EventType eventType);
}
