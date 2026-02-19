package com.payment.controller;

import com.payment.dto.PaymentResponse;
import com.payment.dto.SplitPaymentRequest;
import com.payment.service.SplitPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class SplitPaymentController {

    private final SplitPaymentService splitPaymentService;

    @PostMapping("/split")
    public ResponseEntity<PaymentResponse> processSplitPayment(@Valid @RequestBody SplitPaymentRequest request) {
        PaymentResponse response = splitPaymentService.processSplitPayment(request);
        return ResponseEntity.ok(response);
    }
}

