package com.payment.monitoring.service;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Monitoring Enhancement: Business Metrics Service
 *
 * Central place to record domain-level metrics for paymentprocessing.
 * These appear in Prometheus and can be visualised in Grafana.
 *
 * Metrics registered:
 *  - paymentprocessing.operations.total      — counter per operation + status
 *  - paymentprocessing.active.gauge          — current in-flight operations
 *  - paymentprocessing.operation.duration    — summary timer
 *  - paymentprocessing.errors.total          — error counter per error type
 *  - paymentprocessing.kafka.events.total    — Kafka event counter
 */
@Service
public class BusinessMetricsService {

    private final MeterRegistry meterRegistry;

    // Gauge — currently active operations
    private final AtomicInteger activeOperations = new AtomicInteger(0);

    public BusinessMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        // Register gauge once
        Gauge.builder("paymentprocessing.active.operations", activeOperations, AtomicInteger::get)
             .description("Number of currently active paymentprocessing operations")
             .tag("service", "paymentprocessing")
             .register(meterRegistry);
    }

    /**
     * Record a successful operation completion.
     * @param operationType  e.g. "create", "update", "delete", "query"
     */
    public void recordSuccess(String operationType) {
        Counter.builder("paymentprocessing.operations.total")
               .tag("service", "paymentprocessing")
               .tag("operation", operationType)
               .tag("status", "success")
               .description("Total paymentprocessing operations by type and status")
               .register(meterRegistry)
               .increment();
    }

    /**
     * Record a failed operation.
     * @param operationType  e.g. "create", "update"
     * @param errorType      e.g. "validation", "database", "kafka"
     */
    public void recordFailure(String operationType, String errorType) {
        Counter.builder("paymentprocessing.errors.total")
               .tag("service", "paymentprocessing")
               .tag("operation", operationType)
               .tag("error_type", errorType)
               .description("Total paymentprocessing errors by operation and error type")
               .register(meterRegistry)
               .increment();
    }

    /**
     * Record a Kafka event published or consumed.
     * @param topic      Kafka topic name
     * @param direction  "published" or "consumed"
     */
    public void recordKafkaEvent(String topic, String direction) {
        Counter.builder("paymentprocessing.kafka.events.total")
               .tag("service", "paymentprocessing")
               .tag("topic", topic)
               .tag("direction", direction)
               .description("Total Kafka events for paymentprocessing")
               .register(meterRegistry)
               .increment();
    }

    /**
     * Record operation duration.
     * @param operationType  operation name
     * @param durationMs     elapsed milliseconds
     */
    public void recordDuration(String operationType, long durationMs) {
        meterRegistry.summary("paymentprocessing.operation.duration",
                "service", "paymentprocessing",
                "operation", operationType)
               .record(durationMs);
    }

    /** Mark one more in-flight operation. Call at start of operation. */
    public void incrementActive() { activeOperations.incrementAndGet(); }

    /** Mark one less in-flight operation. Call in finally block. */
    public void decrementActive() { activeOperations.decrementAndGet(); }
}
