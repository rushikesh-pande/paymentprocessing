package com.payment.controller;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.service.PaymentService;
import com.orderprocessing.trace.TraceContextHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for payment processing.
 * TraceId is automatically injected by TraceFilter from trace-context-lib.
 * Full trace is propagated: createorder → orderprocessing → paymentprocessing.
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        String traceId = TraceContextHolder.getTraceId();
        log.info("[{}] Received payment request for order: {}", traceId, request.getOrderId());

        request.setTraceId(traceId);

        PaymentResponse response = paymentService.processPayment(request);
        response.setTraceId(traceId);

        log.info("[{}] Payment processed — status: {}", traceId, response.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable String paymentId) {
        String traceId = TraceContextHolder.getTraceId();
        log.info("[{}] Received get payment status request: {}", traceId, paymentId);

        PaymentResponse response = paymentService.getPaymentStatus(paymentId);
        response.setTraceId(traceId);

        return ResponseEntity.ok(response);
    }
}
