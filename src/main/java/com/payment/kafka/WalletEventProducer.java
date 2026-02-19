package com.payment.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.model.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalletEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String WALLET_RECHARGED_TOPIC = "wallet.recharged";

    public void publishWalletRecharged(Wallet wallet, Double amount) {
        try {
            String message = String.format(
                "{\"walletId\":\"%s\",\"customerId\":\"%s\",\"amount\":%.2f,\"newBalance\":%.2f,\"timestamp\":%d}",
                wallet.getWalletId(), wallet.getCustomerId(), amount, wallet.getBalance(), System.currentTimeMillis()
            );
            kafkaTemplate.send(WALLET_RECHARGED_TOPIC, wallet.getCustomerId(), message);
            log.info("Published wallet recharged event for customer: {}", wallet.getCustomerId());
        } catch (Exception e) {
            log.error("Error publishing wallet recharged event: {}", e.getMessage(), e);
        }
    }
}

