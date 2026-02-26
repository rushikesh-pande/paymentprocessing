package com.payment.giftcard.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Enhancement #14 - Store credit balance response.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StoreCreditResponse {
    private String customerId;
    private Double balance;
    private LocalDateTime lastUpdated;
}
