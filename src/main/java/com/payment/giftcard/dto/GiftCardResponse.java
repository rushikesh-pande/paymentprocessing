package com.payment.giftcard.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Enhancement #14 - Gift card response with balance and status.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GiftCardResponse {
    private Long id;
    private String code;
    private Double originalAmount;
    private Double remainingBalance;
    private String status;
    private String recipientEmail;
    private String recipientName;
    private String message;
    private LocalDateTime purchasedAt;
    private LocalDateTime expiresAt;
    private boolean usable;
}
