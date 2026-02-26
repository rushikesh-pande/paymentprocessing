package com.payment.giftcard.controller;

import com.payment.giftcard.dto.*;
import com.payment.giftcard.service.GiftCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Enhancement #14 - Gift Cards & Store Credit REST API
 */
@RestController
@RequestMapping("/api/v1/gift-cards")
@RequiredArgsConstructor
@Slf4j
public class GiftCardController {

    private final GiftCardService giftCardService;

    /** POST /api/v1/gift-cards — Purchase a new gift card */
    @PostMapping
    public ResponseEntity<GiftCardResponse> purchaseGiftCard(
            @Valid @RequestBody GiftCardPurchaseRequest req) {
        log.info("POST /gift-cards customer={}", req.getCustomerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(giftCardService.purchaseGiftCard(req));
    }

    /** GET /api/v1/gift-cards/{code}/balance — Check gift card balance */
    @GetMapping("/{code}/balance")
    public ResponseEntity<GiftCardResponse> getBalance(@PathVariable String code) {
        return ResponseEntity.ok(giftCardService.getGiftCardBalance(code));
    }

    /** POST /api/v1/gift-cards/redeem — Redeem gift card against an order */
    @PostMapping("/redeem")
    public ResponseEntity<GiftCardResponse> redeemGiftCard(
            @Valid @RequestBody GiftCardRedeemRequest req) {
        log.info("POST /gift-cards/redeem code={} orderId={}", req.getCode(), req.getOrderId());
        return ResponseEntity.ok(giftCardService.redeemGiftCard(req));
    }

    /** GET /api/v1/gift-cards/customer/{customerId} — Get all gift cards for customer */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<GiftCardResponse>> getCustomerGiftCards(
            @PathVariable String customerId) {
        return ResponseEntity.ok(giftCardService.getCustomerGiftCards(customerId));
    }

    /** GET /api/v1/gift-cards/store-credit/{customerId} — Get store credit balance */
    @GetMapping("/store-credit/{customerId}")
    public ResponseEntity<StoreCreditResponse> getStoreCredit(@PathVariable String customerId) {
        return ResponseEntity.ok(giftCardService.getStoreCredit(customerId));
    }

    /** POST /api/v1/gift-cards/store-credit/{customerId}/add — Add store credit */
    @PostMapping("/store-credit/{customerId}/add")
    public ResponseEntity<StoreCreditResponse> addStoreCredit(
            @PathVariable String customerId,
            @RequestParam double amount,
            @RequestParam(defaultValue = "MANUAL") String reason) {
        return ResponseEntity.ok(giftCardService.addStoreCredit(customerId, amount, reason));
    }

    /** POST /api/v1/gift-cards/store-credit/{customerId}/use — Use store credit */
    @PostMapping("/store-credit/{customerId}/use")
    public ResponseEntity<StoreCreditResponse> useStoreCredit(
            @PathVariable String customerId,
            @RequestParam double amount) {
        return ResponseEntity.ok(giftCardService.useStoreCredit(customerId, amount));
    }
}
