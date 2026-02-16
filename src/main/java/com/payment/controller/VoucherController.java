package com.payment.controller;
import com.payment.dto.VoucherValidationResult;
import com.payment.entity.Voucher;
import com.payment.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Slf4j
public class VoucherController {
    private final VoucherService voucherService;
    @PostMapping
    public ResponseEntity<Voucher> createVoucher(@Valid @RequestBody Voucher voucher) {
        log.info("Creating voucher: {}", voucher.getVoucherCode());
        Voucher created = voucherService.createVoucher(voucher);
        return ResponseEntity.ok(created);
    }
    @GetMapping("/{voucherCode}")
    public ResponseEntity<Voucher> getVoucher(@PathVariable String voucherCode) {
        log.info("Fetching voucher: {}", voucherCode);
        Voucher voucher = voucherService.getVoucherByCode(voucherCode);
        return ResponseEntity.ok(voucher);
    }
    @PostMapping("/validate")
    public ResponseEntity<VoucherValidationResult> validateVoucher(
            @RequestParam String voucherCode,
            @RequestParam BigDecimal orderAmount) {
        log.info("Validating voucher {} for amount: {}", voucherCode, orderAmount);
        VoucherValidationResult result = voucherService.validateAndApplyVoucher(voucherCode, orderAmount);
        return ResponseEntity.ok(result);
    }
}
