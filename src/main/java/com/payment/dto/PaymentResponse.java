package com.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    /** TraceId for end-to-end request tracking */
    private String traceId;


    private String paymentId;
    private String orderId;
    private String status;
    private String message;
    private Double amount;
    private String paymentMethod;
    private String transactionId;
}

