package net.maaroufi.paymentlistener;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentListener {

    @PostMapping("/process")
    public ResponseEntity<Map<String, String>> processPayment(@RequestBody Map<String, String> body) {
        String orderId = body.getOrDefault("orderId", "unknown");
        System.out.println("Paiement en cours pour OrderId: " + orderId);
        return ResponseEntity.ok(Map.of("orderId", orderId, "status", "PaymentProcessed"));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("service", "payment-service", "status", "UP"));
    }
}
