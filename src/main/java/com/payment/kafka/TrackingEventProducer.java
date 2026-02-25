package com.payment.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * Enhancement #1 - Order Tracking
 * Publishes payment.completed event so order tracking service can update status.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TrackingEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishPaymentCompleted(String orderId, String paymentId, double amount, String method) {
        String payload = String.format(
            "{\"orderId\":\"%s\",\"paymentId\":\"%s\",\"amount\":%.2f,\"method\":\"%s\",\"event\":\"PAYMENT_COMPLETED\",\"timestamp\":\"%s\"}",
            orderId, paymentId, amount, method, LocalDateTime.now());
        kafkaTemplate.send("payment.completed", orderId, payload);
        log.info("[TRACKING] Published payment.completed event orderId={} paymentId={}", orderId, paymentId);
    }

    public void publishPaymentFailed(String orderId, String reason) {
        String payload = String.format(
            "{\"orderId\":\"%s\",\"reason\":\"%s\",\"event\":\"PAYMENT_FAILED\",\"timestamp\":\"%s\"}",
            orderId, reason, LocalDateTime.now());
        kafkaTemplate.send("payment.failed", orderId, payload);
        log.info("[TRACKING] Published payment.failed event orderId={}", orderId);
    }
}
