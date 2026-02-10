package com.payment.service;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.entity.Payment;
import com.payment.entity.PaymentMethod;
import com.payment.entity.PaymentStatus;
import com.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for order: {}", request.getOrderId());

        String paymentId = generatePaymentId();

        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()))
                .status(PaymentStatus.PROCESSING)
                .build();

        log.info("Processing payment through gateway...");
        boolean paymentSuccess = processWithGateway(request);

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setPaymentGatewayResponse("Payment successful");
            payment.setCompletedAt(LocalDateTime.now());
            log.info("Payment completed successfully: {}", paymentId);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setPaymentGatewayResponse("Payment failed");
            log.error("Payment failed: {}", paymentId);
        }

        Payment saved = paymentRepository.save(payment);
        return mapToResponse(saved, paymentSuccess ? "Payment completed successfully" : "Payment failed");
    }

    public PaymentResponse getPaymentStatus(String paymentId) {
        log.info("Fetching payment status: {}", paymentId);
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        return mapToResponse(payment, null);
    }

    private boolean processWithGateway(PaymentRequest request) {
        return true;
    }

    private String generatePaymentId() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PaymentResponse mapToResponse(Payment payment, String message) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .status(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .completedAt(payment.getCompletedAt())
                .message(message)
                .build();
    }
}

