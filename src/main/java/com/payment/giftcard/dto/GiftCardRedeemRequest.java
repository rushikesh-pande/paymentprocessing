package com.payment.giftcard.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Enhancement #14 - Request to redeem a gift card against an order.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GiftCardRedeemRequest {
    @NotBlank private String code;
    @NotBlank private String customerId;
    @NotBlank private String orderId;
    @NotNull @Min(1)
              private Double amountToRedeem;
}
