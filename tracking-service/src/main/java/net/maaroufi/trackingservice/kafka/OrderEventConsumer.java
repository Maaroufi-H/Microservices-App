package net.maaroufi.trackingservice.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.maaroufi.core.tracking.CoreEventType;
import net.maaroufi.trackingservice.services.TrackingService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listens to the "order-events" Kafka topic.
 *
 * When a TRANSACTION_COMPLETE event arrives, it looks up all BehaviorEvents
 * for the same sessionId and marks them converted=true.
 *
 * This implements the "delayed labeling" pattern:
 *   - At browse time, converted=false (we don't know yet if user will buy)
 *   - After purchase, we retroactively label the session events converted=true
 *   - Azure ML uses this as the training label for the recommendation model
 */
@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final TrackingService trackingService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderEventConsumer(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @KafkaListener(topics = "${app.kafka.topic.order-events:order-events}",
                   groupId = "${spring.kafka.consumer.group-id:tracking-service}")
    public void onOrderEvent(ConsumerRecord<String, String> record) {
        try {
            JsonNode payload = objectMapper.readTree(record.value());
            String eventType = payload.path("eventType").asText();
            String sessionId  = payload.path("sessionId").asText();

            if (CoreEventType.TRANSACTION_COMPLETE.name().equals(eventType)
                    && sessionId != null && !sessionId.isBlank()) {
                log.info("TRANSACTION_COMPLETE received for session={} — marking converted=true", sessionId);
                trackingService.markSessionAsConverted(sessionId);
            }
        } catch (Exception e) {
            log.warn("Failed to process order event: key={}, error={}", record.key(), e.getMessage());
        }
    }
}
