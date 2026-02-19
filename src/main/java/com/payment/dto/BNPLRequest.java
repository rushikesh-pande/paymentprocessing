package com.payment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BNPLRequest {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Provider is required")
    private String provider; // KLARNA, AFTERPAY, AFFIRM

    @NotNull(message = "Number of installments is required")
    @Min(value = 2, message = "Minimum 2 installments required")
    @Max(value = 12, message = "Maximum 12 installments allowed")
    private Integer installments;
}

