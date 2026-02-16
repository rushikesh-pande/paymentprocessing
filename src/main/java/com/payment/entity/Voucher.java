package com.payment.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "vouchers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String voucherCode;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minOrderAmount;
    @Column(nullable = false)
    private LocalDateTime validFrom;
    @Column(nullable = false)
    private LocalDateTime validUntil;
    @Column(nullable = false)
    private Boolean active;
    @Column(nullable = false)
    private Integer maxUsageCount;
    @Column(nullable = false)
    private Integer currentUsageCount;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (active == null) {
            active = true;
        }
        if (currentUsageCount == null) {
            currentUsageCount = 0;
        }
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return active 
            && now.isAfter(validFrom) 
            && now.isBefore(validUntil)
            && currentUsageCount < maxUsageCount;
    }
}
