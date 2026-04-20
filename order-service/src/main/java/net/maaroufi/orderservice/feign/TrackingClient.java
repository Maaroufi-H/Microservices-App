package net.maaroufi.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "TRACKING-SERVICE", path = "/api/tracking")
public interface TrackingClient {

    @PostMapping("/convert/{sessionId}")
    void markConverted(@PathVariable String sessionId);
}
