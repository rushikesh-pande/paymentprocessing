package com.payment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletPaymentRequest {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Wallet type is required")
    private String walletType; // e.g., PAYTM, PHONEPE, GOOGLEPAY, AMAZONPAY

    @NotBlank(message = "Wallet ID is required")
    private String walletId;

    private String customerPhone;

    private String customerEmail;
}

