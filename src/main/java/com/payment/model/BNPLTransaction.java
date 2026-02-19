package com.payment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bnpl_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BNPLTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bnplId;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String provider; // KLARNA, AFTERPAY, etc.

    @Column(nullable = false)
    private Integer installments;

    @Column(nullable = false)
    private Double installmentAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BNPLStatus status;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column
    private LocalDateTime paidDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (bnplId == null) {
            bnplId = "BNPL-" + System.currentTimeMillis();
        }
    }
}

