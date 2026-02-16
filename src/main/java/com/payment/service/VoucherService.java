package com.payment.service;
import com.payment.dto.VoucherValidationResult;
import com.payment.entity.Voucher;
import com.payment.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherService {
    private final VoucherRepository voucherRepository;
    @Transactional
    public VoucherValidationResult validateAndApplyVoucher(String voucherCode, BigDecimal orderAmount) {
        log.info("Validating voucher: {} for order amount: {}", voucherCode, orderAmount);
        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            return VoucherValidationResult.builder()
                    .valid(false)
                    .message("Voucher code is empty")
                    .finalAmount(orderAmount)
                    .build();
        }
        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode.toUpperCase())
                .orElse(null);
        if (voucher == null) {
            log.warn("Voucher not found: {}", voucherCode);
            return VoucherValidationResult.builder()
                    .valid(false)
                    .message("Invalid voucher code")
                    .finalAmount(orderAmount)
                    .build();
        }
        if (!voucher.isValid()) {
            log.warn("Voucher is not valid: {}", voucherCode);
            return VoucherValidationResult.builder()
                    .valid(false)
                    .message("Voucher is expired or not active")
                    .finalAmount(orderAmount)
                    .voucherCode(voucherCode)
                    .build();
        }
        if (orderAmount.compareTo(voucher.getMinOrderAmount()) < 0) {
            log.warn("Order amount {} is less than minimum required: {}", orderAmount, voucher.getMinOrderAmount());
            return VoucherValidationResult.builder()
                    .valid(false)
                    .message(String.format("Minimum order amount of %.2f required to use this voucher", voucher.getMinOrderAmount()))
                    .finalAmount(orderAmount)
                    .voucherCode(voucherCode)
                    .build();
        }
        BigDecimal discountAmount = orderAmount
                .multiply(voucher.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        if (discountAmount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
            discountAmount = voucher.getMaxDiscountAmount();
        }
        BigDecimal finalAmount = orderAmount.subtract(discountAmount);
        voucher.setCurrentUsageCount(voucher.getCurrentUsageCount() + 1);
        voucherRepository.save(voucher);
        log.info("Voucher applied successfully. Discount: {}, Final amount: {}", discountAmount, finalAmount);
        return VoucherValidationResult.builder()
                .valid(true)
                .message("Voucher applied successfully")
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .voucherCode(voucherCode)
                .build();
    }
    @Transactional(readOnly = true)
    public Voucher getVoucherByCode(String voucherCode) {
        return voucherRepository.findByVoucherCode(voucherCode.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Voucher not found: " + voucherCode));
    }
    @Transactional
    public Voucher createVoucher(Voucher voucher) {
        voucher.setVoucherCode(voucher.getVoucherCode().toUpperCase());
        log.info("Creating new voucher: {}", voucher.getVoucherCode());
        return voucherRepository.save(voucher);
    }
}
