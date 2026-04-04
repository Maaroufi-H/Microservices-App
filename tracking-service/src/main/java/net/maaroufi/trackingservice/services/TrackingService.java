package net.maaroufi.trackingservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.maaroufi.trackingservice.dto.BehaviorEventDTO;
import net.maaroufi.trackingservice.dto.UtmParameters;
import net.maaroufi.trackingservice.entities.BehaviorEvent;
import net.maaroufi.trackingservice.repository.BehaviorEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TrackingService {

    private final BehaviorEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic.behavior-events}")
    private String behaviorEventsTopic;

    public TrackingService(BehaviorEventRepository repository,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public BehaviorEvent ingestEvent(BehaviorEventDTO dto) {
        BehaviorEvent event = new BehaviorEvent();

        event.setSessionId(dto.getSessionId());
        event.setCustomerId(dto.getCustomerId());
        event.setEventType(dto.getEventType());
        event.setProductId(dto.getProductId());
        event.setSearchQuery(dto.getSearchQuery());
        event.setPageUrl(dto.getPageUrl());
        event.setClientTimestamp(dto.getClientTimestamp());
        event.setServerTimestamp(Instant.now());

        UtmParameters utm = dto.getUtm();
        if (utm != null) {
            event.setUtmSource(utm.getUtmSource());
            event.setUtmMedium(utm.getUtmMedium());
            event.setUtmCampaign(utm.getUtmCampaign());
            event.setUtmContent(utm.getUtmContent());
            event.setUtmTerm(utm.getUtmTerm());
        }

        try {
            event.setRawJson(objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            event.setRawJson("{}");
        }

        BehaviorEvent saved = repository.save(event);

        // Publish to Kafka for downstream Azure ingestion
        try {
            kafkaTemplate.send(behaviorEventsTopic, saved.getSessionId(), objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            kafkaTemplate.send(behaviorEventsTopic, saved.getSessionId(), event.getRawJson());
        }

        return saved;
    }
}
