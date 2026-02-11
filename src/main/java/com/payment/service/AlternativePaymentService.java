package com.payment.service;

import com.payment.dto.CardPaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.dto.UpiPaymentRequest;
import com.payment.dto.WalletPaymentRequest;
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

/**
 * Enhanced Payment Service with Alternative Payment Methods
 * Enhancement: Card Payment, UPI Payment, and Wallet Payment support
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlternativePaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse processCardPayment(CardPaymentRequest request) {
        log.info("Processing card payment for order: {}", request.getOrderId());

        String paymentId = generatePaymentId();

        // Validate card details
        if (!validateCard(request)) {
            throw new RuntimeException("Invalid card details");
        }

        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .paymentMethod(request.getCardType().equalsIgnoreCase("CREDIT") ? 
                        PaymentMethod.CREDIT_CARD : PaymentMethod.DEBIT_CARD)
                .status(PaymentStatus.PROCESSING)
                .build();

        // Process with card payment gateway
        log.info("Processing card payment through gateway...");
        boolean paymentSuccess = processCardWithGateway(request);

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setPaymentGatewayResponse("Card payment successful - " + maskCardNumber(request.getCardNumber()));
            payment.setCompletedAt(LocalDateTime.now());
            log.info("Card payment completed successfully: {}", paymentId);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setPaymentGatewayResponse("Card payment failed");
            log.error("Card payment failed: {}", paymentId);
        }

        Payment saved = paymentRepository.save(payment);
        return mapToResponse(saved, paymentSuccess ? "Card payment completed successfully" : "Card payment failed");
    }

    @Transactional
    public PaymentResponse processUpiPayment(UpiPaymentRequest request) {
        log.info("Processing UPI payment for order: {}", request.getOrderId());

        String paymentId = generatePaymentId();

        // Validate UPI ID
        if (!validateUpiId(request.getUpiId())) {
            throw new RuntimeException("Invalid UPI ID format");
        }

        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .paymentMethod(PaymentMethod.UPI)
                .status(PaymentStatus.PROCESSING)
                .build();

        // Process with UPI payment gateway
        log.info("Processing UPI payment through gateway...");
        boolean paymentSuccess = processUpiWithGateway(request);

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(generateUpiTransactionId());
            payment.setPaymentGatewayResponse("UPI payment successful - " + request.getUpiId());
            payment.setCompletedAt(LocalDateTime.now());
            log.info("UPI payment completed successfully: {}", paymentId);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setPaymentGatewayResponse("UPI payment failed");
            log.error("UPI payment failed: {}", paymentId);
        }

        Payment saved = paymentRepository.save(payment);
        return mapToResponse(saved, paymentSuccess ? "UPI payment completed successfully" : "UPI payment failed");
    }

    @Transactional
    public PaymentResponse processWalletPayment(WalletPaymentRequest request) {
        log.info("Processing wallet payment for order: {} using wallet type: {}", 
                request.getOrderId(), request.getWalletType());

        String paymentId = generatePaymentId();

        // Validate wallet details
        if (!validateWallet(request)) {
            throw new RuntimeException("Invalid wallet details");
        }

        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .paymentMethod(PaymentMethod.WALLET)
                .status(PaymentStatus.PROCESSING)
                .build();

        // Process with wallet payment gateway
        log.info("Processing wallet payment through {} gateway...", request.getWalletType());
        boolean paymentSuccess = processWalletWithGateway(request);

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(generateWalletTransactionId(request.getWalletType()));
            payment.setPaymentGatewayResponse("Wallet payment successful - " + request.getWalletType() + " - " + maskWalletId(request.getWalletId()));
            payment.setCompletedAt(LocalDateTime.now());
            log.info("Wallet payment completed successfully: {}", paymentId);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setPaymentGatewayResponse("Wallet payment failed");
            log.error("Wallet payment failed: {}", paymentId);
        }

        Payment saved = paymentRepository.save(payment);
        return mapToResponse(saved, paymentSuccess ? "Wallet payment completed successfully" : "Wallet payment failed");
    }

    private boolean validateCard(CardPaymentRequest request) {
        // Validate card number (Luhn algorithm simulation)
        String cardNumber = request.getCardNumber().replaceAll("\\s+", "");
        if (cardNumber.length() < 13 || cardNumber.length() > 19) {
            log.warn("Invalid card number length");
            return false;
        }

        // Validate expiry date
        if (request.getExpiryMonth() < 1 || request.getExpiryMonth() > 12) {
            log.warn("Invalid expiry month");
            return false;
        }

        // Validate CVV
        if (request.getCvv().length() < 3 || request.getCvv().length() > 4) {
            log.warn("Invalid CVV");
            return false;
        }

        return true;
    }

    private boolean validateUpiId(String upiId) {
        // Validate UPI ID format: username@bankname
        return upiId != null && upiId.matches("^[a-zA-Z0-9.\\-_]{2,}@[a-zA-Z]{2,}$");
    }

    private boolean validateWallet(WalletPaymentRequest request) {
        // Validate wallet type
        String walletType = request.getWalletType().toUpperCase();
        if (!walletType.matches("^(PAYTM|PHONEPE|GOOGLEPAY|AMAZONPAY|MOBIKWIK|FREECHARGE)$")) {
            log.warn("Invalid wallet type: {}", request.getWalletType());
            return false;
        }

        // Validate wallet ID
        if (request.getWalletId() == null || request.getWalletId().trim().isEmpty()) {
            log.warn("Wallet ID is required");
            return false;
        }

        return true;
    }

    private boolean processCardWithGateway(CardPaymentRequest request) {
        // Simulate card payment gateway processing
        // In real implementation, this would call actual payment gateway API
        return true;
    }

    private boolean processUpiWithGateway(UpiPaymentRequest request) {
        // Simulate UPI payment gateway processing
        // In real implementation, this would call UPI payment API
        return true;
    }

    private boolean processWalletWithGateway(WalletPaymentRequest request) {
        // Simulate wallet payment gateway processing
        // In real implementation, this would call actual wallet API (PayTm, PhonePe, etc.)
        return true;
    }

    private String maskCardNumber(String cardNumber) {
        String cleaned = cardNumber.replaceAll("\\s+", "");
        if (cleaned.length() < 4) return "****";
        return "**** **** **** " + cleaned.substring(cleaned.length() - 4);
    }

    private String maskWalletId(String walletId) {
        if (walletId.length() <= 4) return "****";
        return walletId.substring(0, 2) + "****" + walletId.substring(walletId.length() - 2);
    }

    private String generatePaymentId() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateUpiTransactionId() {
        return "UPI" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateWalletTransactionId(String walletType) {
        return walletType.toUpperCase() + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
