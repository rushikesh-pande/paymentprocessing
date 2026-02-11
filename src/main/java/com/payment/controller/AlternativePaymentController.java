package com.payment.controller;

import com.payment.dto.CardPaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.dto.UpiPaymentRequest;
import com.payment.dto.WalletPaymentRequest;
import com.payment.service.AlternativePaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for alternative payment methods
 * Enhancement: Card Payment, UPI Payment, and Wallet Payment endpoints
 */
@RestController
@RequestMapping("/api/v1/payments/alternative")
@RequiredArgsConstructor
@Slf4j
public class AlternativePaymentController {

    private final AlternativePaymentService alternativePaymentService;

    @PostMapping("/card")
    public ResponseEntity<PaymentResponse> processCardPayment(
            @Valid @RequestBody CardPaymentRequest request) {
        log.info("Received card payment request for order: {}", request.getOrderId());

        PaymentResponse response = alternativePaymentService.processCardPayment(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/upi")
    public ResponseEntity<PaymentResponse> processUpiPayment(
            @Valid @RequestBody UpiPaymentRequest request) {
        log.info("Received UPI payment request for order: {}", request.getOrderId());

        PaymentResponse response = alternativePaymentService.processUpiPayment(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/wallet")
    public ResponseEntity<PaymentResponse> processWalletPayment(
            @Valid @RequestBody WalletPaymentRequest request) {
        log.info("Received wallet payment request for order: {} using {}", 
                request.getOrderId(), request.getWalletType());
        
        PaymentResponse response = alternativePaymentService.processWalletPayment(request);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
