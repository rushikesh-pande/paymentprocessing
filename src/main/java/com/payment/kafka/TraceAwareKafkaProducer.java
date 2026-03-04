package com.payment.kafka;

import com.orderprocessing.trace.KafkaTraceHeaders;
import com.orderprocessing.trace.TraceContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Trace-aware Kafka producer for paymentprocessing service.
 * Injects TraceContext into every Kafka message header for downstream correlation.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TraceAwareKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendWithTrace(String topic, String key, String payload) {
        var ctx = TraceContextHolder.get();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, payload);

        if (ctx != null) {
            KafkaTraceHeaders.inject(ctx, record.headers());
            log.info("[{}] Sending payment Kafka event to topic={} key={}", ctx.getTraceId(), topic, key);
        } else {
            log.warn("[TRACE-MISSING] Sending Kafka message without trace context to topic={}", topic);
        }

        kafkaTemplate.send(record);
    }
}
