package net.maaroufi.paymentlistener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentListener {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "order-events", groupId = "payment-group")
    public void listenOrderEvents(String message) {
        System.out.println("Reçu : " + message);

        String orderId = message.split(":")[1];

        try {
            // Simuler logique de paiement
            System.out.println("Paiement en cours pour OrderId: " + orderId);
            // Si OK
            kafkaTemplate.send("payment-events", "PaymentProcessed:" + orderId);
        } catch (Exception e) {
            kafkaTemplate.send("payment-events", "PaymentFailed:" + orderId);
        }
    }
}
