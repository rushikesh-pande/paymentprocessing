package com.payment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EMIRequest {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Tenure months is required")
    @Min(value = 3, message = "Minimum tenure is 3 months")
    @Max(value = 24, message = "Maximum tenure is 24 months")
    private Integer tenureMonths;

    @NotNull(message = "Interest rate is required")
    @PositiveOrZero(message = "Interest rate must be zero or positive")
    private Double interestRate;
}

