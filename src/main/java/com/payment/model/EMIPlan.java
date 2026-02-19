package com.payment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "emi_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EMIPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String emiPlanId;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private Integer tenureMonths;

    @Column(nullable = false)
    private Double monthlyInstallment;

    @Column(nullable = false)
    private Double interestRate;

    @Column(nullable = false)
    private Integer remainingInstallments;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EMIStatus status;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime nextDueDate;

    @Column
    private LocalDateTime completedDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (emiPlanId == null) {
            emiPlanId = "EMI-" + System.currentTimeMillis();
        }
    }
}

