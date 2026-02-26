package com.payment.giftcard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Enhancement #14 - Store credit balance per customer (for returns/refunds).
 */
@Entity
@Table(name = "store_credits")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StoreCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String customerId;

    @Column(nullable = false)
    private Double balance;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist @PreUpdate
    protected void onUpdate() { lastUpdated = LocalDateTime.now(); }

    public void addCredit(double amount) {
        this.balance = (this.balance == null ? 0 : this.balance) + amount;
    }

    public boolean hasSufficientCredit(double amount) {
        return balance != null && balance >= amount;
    }
}
