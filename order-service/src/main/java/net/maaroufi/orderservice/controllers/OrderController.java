package net.maaroufi.orderservice.controllers;

import net.maaroufi.orderservice.feign.TrackingClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final TrackingClient trackingClient;

    public OrderController(TrackingClient trackingClient) {
        this.trackingClient = trackingClient;
    }

    @GetMapping("/create")
    public String createOrder(@RequestParam String orderId, @RequestParam(required = false) String sessionId) {
        if (sessionId != null && !sessionId.isBlank()) {
            trackingClient.markConverted(sessionId);
        }
        return "Commande créée : " + orderId;
    }
}
