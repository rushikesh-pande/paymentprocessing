package com.payment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SplitPaymentRequest {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private Double totalAmount;

    @NotNull(message = "Wallet amount is required")
    @PositiveOrZero(message = "Wallet amount must be zero or positive")
    private Double walletAmount;

    @NotNull(message = "Card amount is required")
    @PositiveOrZero(message = "Card amount must be zero or positive")
    private Double cardAmount;

    private String cardNumber;
    private String cardCvv;
    private String cardExpiry;
}

