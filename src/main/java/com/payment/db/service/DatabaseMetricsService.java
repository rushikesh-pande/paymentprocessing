package com.payment.db.service;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Database Optimisation Enhancement: Database Metrics Service
 *
 * Tracks cache and query performance metrics for paymentprocessing.
 * Exposed to Prometheus via /actuator/prometheus.
 *
 * Metrics:
 *  - paymentprocessing_cache_hits_total       — Redis cache hits
 *  - paymentprocessing_cache_misses_total     — Redis cache misses (DB queries)
 *  - paymentprocessing_db_queries_total       — Total DB queries by type
 *  - paymentprocessing_db_slow_queries_total  — Queries above 500ms
 *  - paymentprocessing_connection_pool_active — HikariCP active connections
 */
@Service
public class DatabaseMetricsService {

    private final MeterRegistry meterRegistry;
    private final AtomicLong activeConnections = new AtomicLong(0);

    public DatabaseMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        Gauge.builder("paymentprocessing.connection.pool.active", activeConnections, AtomicLong::get)
             .description("Active HikariCP connections for paymentprocessing")
             .tag("service", "paymentprocessing")
             .register(meterRegistry);
    }

    public void recordCacheHit(String cacheName) {
        Counter.builder("paymentprocessing.cache.hits.total")
               .tag("service", "paymentprocessing").tag("cache", cacheName)
               .description("Redis cache hits for paymentprocessing")
               .register(meterRegistry).increment();
    }

    public void recordCacheMiss(String cacheName) {
        Counter.builder("paymentprocessing.cache.misses.total")
               .tag("service", "paymentprocessing").tag("cache", cacheName)
               .description("Redis cache misses for paymentprocessing (DB fallback)")
               .register(meterRegistry).increment();
    }

    public void recordDbQuery(String queryType) {
        Counter.builder("paymentprocessing.db.queries.total")
               .tag("service", "paymentprocessing").tag("type", queryType)
               .description("DB queries for paymentprocessing")
               .register(meterRegistry).increment();
    }

    public void recordSlowQuery(String queryType, long ms) {
        Counter.builder("paymentprocessing.db.slow.queries.total")
               .tag("service", "paymentprocessing").tag("type", queryType)
               .description("DB queries exceeding 500ms for paymentprocessing")
               .register(meterRegistry).increment();
        meterRegistry.summary("paymentprocessing.db.query.duration",
                "service", "paymentprocessing", "type", queryType).record(ms);
    }

    public void setActiveConnections(long count) {
        activeConnections.set(count);
    }
}
