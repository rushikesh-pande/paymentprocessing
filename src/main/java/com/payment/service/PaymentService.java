package com.payment.service;
import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.dto.VoucherValidationResult;
import com.payment.entity.Payment;
import com.payment.entity.PaymentMethod;
import com.payment.entity.PaymentStatus;
import com.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final VoucherService voucherService;
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for order: {}", request.getOrderId());
        String paymentId = generatePaymentId();
        BigDecimal originalAmount = request.getAmount();
        BigDecimal finalAmount = originalAmount;
        BigDecimal discountAmount = BigDecimal.ZERO;
        String voucherCode = null;
        String responseMessage = "Payment completed successfully";
        // Apply voucher discount if provided
        if (request.getVoucherCode() != null && !request.getVoucherCode().trim().isEmpty()) {
            VoucherValidationResult voucherResult = voucherService.validateAndApplyVoucher(
                    request.getVoucherCode(), 
                    originalAmount
            );
            if (voucherResult.isValid()) {
                discountAmount = voucherResult.getDiscountAmount();
                finalAmount = voucherResult.getFinalAmount();
                voucherCode = voucherResult.getVoucherCode();
                responseMessage = String.format("Payment completed successfully with %.2f discount applied", discountAmount);
                log.info("Voucher {} applied. Discount: {}, Final amount: {}", voucherCode, discountAmount, finalAmount);
            } else {
                log.warn("Voucher validation failed: {}", voucherResult.getMessage());
                responseMessage = "Payment completed but voucher was invalid: " + voucherResult.getMessage();
            }
        }
        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .orderId(request.getOrderId())
                .amount(originalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .voucherCode(voucherCode)
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()))
                .status(PaymentStatus.PROCESSING)
                .build();
        log.info("Processing payment through gateway for final amount: {}", finalAmount);
        boolean paymentSuccess = processWithGateway(request, finalAmount);
        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setPaymentGatewayResponse("Payment successful");
            payment.setCompletedAt(LocalDateTime.now());
            log.info("Payment completed successfully: {}", paymentId);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setPaymentGatewayResponse("Payment failed");
            responseMessage = "Payment failed";
            log.error("Payment failed: {}", paymentId);
        }
        Payment saved = paymentRepository.save(payment);
        return mapToResponse(saved, responseMessage);
    }
    public PaymentResponse getPaymentStatus(String paymentId) {
        log.info("Fetching payment status: {}", paymentId);
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        return mapToResponse(payment, null);
    }
    private boolean processWithGateway(PaymentRequest request, BigDecimal amount) {
        // Simulate payment gateway processing
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
                .discountAmount(payment.getDiscountAmount())
                .finalAmount(payment.getFinalAmount())
                .voucherCode(payment.getVoucherCode())
                .paymentMethod(payment.getPaymentMethod().name())
                .status(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .completedAt(payment.getCompletedAt())
                .message(message)
                .build();
    }
}
