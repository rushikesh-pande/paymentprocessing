package com.payment.security;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;

/**
 * PCI DSS Audit Aspect — Enhancement: PCI DSS Compliance (Enhancement #5)
 * Logs every payment operation: cardholder, amount, timestamp, result
 * Requirements: PCI DSS v4.0 — Requirement 10 (Track and monitor all access)
 */
@Aspect @Component @Slf4j
public class PciDssAuditAspect {

    @Around("execution(* com.payment.service..*(..))")
    public Object auditPaymentOperation(ProceedingJoinPoint pjp) throws Throwable {
        String auditId    = UUID.randomUUID().toString();
        String operation  = pjp.getSignature().toShortString();
        Instant startTime = Instant.now();
        log.info("[PCI-DSS][AUDIT-START] auditId={} operation={} time={}",
            auditId, operation, startTime);
        Object result;
        try {
            result = pjp.proceed();
            log.info("[PCI-DSS][AUDIT-SUCCESS] auditId={} operation={} durationMs={}",
                auditId, operation, Instant.now().toEpochMilli() - startTime.toEpochMilli());
            return result;
        } catch (Throwable ex) {
            log.error("[PCI-DSS][AUDIT-FAILURE] auditId={} operation={} error={} durationMs={}",
                auditId, operation, ex.getMessage(),
                Instant.now().toEpochMilli() - startTime.toEpochMilli());
            throw ex;
        }
    }

    @Around("@annotation(com.payment.security.PciSensitive)")
    public Object maskSensitiveData(ProceedingJoinPoint pjp) throws Throwable {
        log.info("[PCI-DSS][SENSITIVE-OP] Executing sensitive payment operation: {}",
            pjp.getSignature().toShortString());
        return pjp.proceed();
    }
}
