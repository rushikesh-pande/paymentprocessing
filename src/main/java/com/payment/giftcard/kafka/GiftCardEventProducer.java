package com.payment.giftcard.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * Enhancement #14 - Gift Cards & Store Credit Kafka Producer
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GiftCardEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /** Publish when a gift card is purchased */
    public void publishGiftCardPurchased(String code, String customerId,
                                         String recipientEmail, double amount) {
        String payload = String.format(
            "{\"code\":\"%s\",\"purchasedBy\":\"%s\",\"recipientEmail\":\"%s\"," +
            "\"amount\":%.2f,\"event\":\"GIFTCARD_PURCHASED\",\"timestamp\":\"%s\"}",
            code, customerId, recipientEmail, amount, LocalDateTime.now());
        kafkaTemplate.send("giftcard.purchased", code, payload);
        log.info("[GIFTCARD] Published giftcard.purchased code={} amount={}", code, amount);
    }

    /** Publish when a gift card is redeemed */
    public void publishGiftCardRedeemed(String code, String customerId,
                                        String orderId, double amountUsed, double remaining) {
        String payload = String.format(
            "{\"code\":\"%s\",\"customerId\":\"%s\",\"orderId\":\"%s\"," +
            "\"amountUsed\":%.2f,\"remainingBalance\":%.2f," +
            "\"event\":\"GIFTCARD_REDEEMED\",\"timestamp\":\"%s\"}",
            code, customerId, orderId, amountUsed, remaining, LocalDateTime.now());
        kafkaTemplate.send("giftcard.redeemed", code, payload);
        log.info("[GIFTCARD] Published giftcard.redeemed code={} orderId={} used={}", code, orderId, amountUsed);
    }

    /** Publish when store credit is added to customer account */
    public void publishCreditAdded(String customerId, double amount, String reason) {
        String payload = String.format(
            "{\"customerId\":\"%s\",\"amount\":%.2f,\"reason\":\"%s\"," +
            "\"event\":\"CREDIT_ADDED\",\"timestamp\":\"%s\"}",
            customerId, amount, reason, LocalDateTime.now());
        kafkaTemplate.send("credit.added", customerId, payload);
        log.info("[GIFTCARD] Published credit.added customerId={} amount={}", customerId, amount);
    }
}
