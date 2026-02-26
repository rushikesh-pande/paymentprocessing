package com.payment.giftcard.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Enhancement #14 - Request to purchase a gift card.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GiftCardPurchaseRequest {
    @NotBlank  private String customerId;
    @NotNull @Min(10) @Max(10000)
               private Double amount;
    @Email @NotBlank
               private String recipientEmail;
    private String recipientName;
    private String message;
}
