package com.payment.kafka;

import com.payment.model.EMIPlan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EMIEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String EMI_APPROVED_TOPIC = "emi.approved";

    public void publishEMIApproved(EMIPlan emiPlan) {
        try {
            String message = String.format(
                "{\"emiPlanId\":\"%s\",\"orderId\":\"%s\",\"customerId\":\"%s\",\"amount\":%.2f,\"tenure\":%d,\"installment\":%.2f}",
                emiPlan.getEmiPlanId(), emiPlan.getOrderId(), emiPlan.getCustomerId(), 
                emiPlan.getTotalAmount(), emiPlan.getTenureMonths(), emiPlan.getMonthlyInstallment()
            );
            kafkaTemplate.send(EMI_APPROVED_TOPIC, emiPlan.getOrderId(), message);
            log.info("Published EMI approved event for order: {}", emiPlan.getOrderId());
        } catch (Exception e) {
            log.error("Error publishing EMI approved event: {}", e.getMessage(), e);
        }
    }
}

