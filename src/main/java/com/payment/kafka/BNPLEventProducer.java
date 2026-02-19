package com.payment.kafka;

import com.payment.model.BNPLTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BNPLEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String BNPL_APPROVED_TOPIC = "bnpl.approved";

    public void publishBNPLApproved(BNPLTransaction bnpl) {
        try {
            String message = String.format(
                "{\"bnplId\":\"%s\",\"orderId\":\"%s\",\"customerId\":\"%s\",\"amount\":%.2f,\"provider\":\"%s\",\"installments\":%d}",
                bnpl.getBnplId(), bnpl.getOrderId(), bnpl.getCustomerId(), 
                bnpl.getAmount(), bnpl.getProvider(), bnpl.getInstallments()
            );
            kafkaTemplate.send(BNPL_APPROVED_TOPIC, bnpl.getOrderId(), message);
            log.info("Published BNPL approved event for order: {}", bnpl.getOrderId());
        } catch (Exception e) {
            log.error("Error publishing BNPL approved event: {}", e.getMessage(), e);
        }
    }
}

