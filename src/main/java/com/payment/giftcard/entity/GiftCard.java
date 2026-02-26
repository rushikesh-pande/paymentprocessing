package com.payment.giftcard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Enhancement #14 - Gift Cards & Store Credit
 * Represents a purchasable/redeemable gift card.
 */
@Entity
@Table(name = "gift_cards")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GiftCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;            // e.g., "GIFT-XMAS-2026-ABCD"

    @Column(nullable = false)
    private Double originalAmount;

    @Column(nullable = false)
    private Double remainingBalance;

    @Enumerated(EnumType.STRING)
    private GiftCardStatus status;

    private String purchasedByCustomerId;
    private String recipientEmail;       // email to send gift card to
    private String recipientName;
    private String message;              // personalised message

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @PrePersist
    protected void onCreate() {
        purchasedAt = LocalDateTime.now();
        if (expiresAt == null) expiresAt = LocalDateTime.now().plusYears(1);
        if (status == null) status = GiftCardStatus.ACTIVE;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isUsable() {
        return status == GiftCardStatus.ACTIVE && !isExpired() && remainingBalance > 0;
    }

    public enum GiftCardStatus { ACTIVE, REDEEMED, EXPIRED, CANCELLED }
}
