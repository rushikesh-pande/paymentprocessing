package com.payment.service;

import com.payment.dto.PaymentResponse;
import com.payment.dto.SplitPaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SplitPaymentService {

    private final WalletService walletService;

    @Transactional
    public PaymentResponse processSplitPayment(SplitPaymentRequest request) {
        log.info("Processing split payment for order: {}", request.getOrderId());

        // Validate amounts
        Double total = request.getWalletAmount() + request.getCardAmount();
        if (!total.equals(request.getTotalAmount())) {
            throw new IllegalArgumentException("Sum of wallet and card amounts must equal total amount");
        }

        PaymentResponse response = new PaymentResponse();
        response.setOrderId(request.getOrderId());
        response.setAmount(request.getTotalAmount());
        response.setPaymentMethod("SPLIT_PAYMENT");

        try {
            // Debit from wallet if wallet amount > 0
            if (request.getWalletAmount() > 0) {
                boolean walletSuccess = walletService.debitWallet(
                    request.getCustomerId(),
                    request.getWalletAmount(),
                    request.getOrderId(),
                    "Split payment - wallet portion"
                );

                if (!walletSuccess) {
                    throw new RuntimeException("Insufficient wallet balance");
                }
                log.info("Wallet portion debited: {}", request.getWalletAmount());
            }

            // Process card payment if card amount > 0
            if (request.getCardAmount() > 0) {
                // Mock card payment processing
                log.info("Processing card payment: {}", request.getCardAmount());
                // In real system, integrate with payment gateway
            }

            response.setStatus("SUCCESS");
            response.setMessage("Split payment processed successfully");
            response.setPaymentId("SPY-" + System.currentTimeMillis());
            response.setTransactionId("TXN-" + System.currentTimeMillis());

            log.info("Split payment completed for order: {}", request.getOrderId());

        } catch (Exception e) {
            log.error("Split payment failed: {}", e.getMessage(), e);
            response.setStatus("FAILED");
            response.setMessage("Split payment failed: " + e.getMessage());
        }

        return response;
    }
}

