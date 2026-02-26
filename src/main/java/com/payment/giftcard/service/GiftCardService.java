package com.payment.giftcard.service;

import com.payment.giftcard.dto.*;
import com.payment.giftcard.entity.*;
import com.payment.giftcard.kafka.GiftCardEventProducer;
import com.payment.giftcard.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhancement #14 - Gift Cards & Store Credit Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCardService {

    private final GiftCardRepository giftCardRepo;
    private final StoreCreditRepository storeCreditRepo;
    private final GiftCardEventProducer eventProducer;

    // ── Purchase a gift card ──────────────────────────────────────
    @Transactional
    public GiftCardResponse purchaseGiftCard(GiftCardPurchaseRequest req) {
        log.info("[GIFTCARD] Purchasing gift card for customer={} amount={} recipient=={}",
                req.getCustomerId(), req.getAmount(), req.getRecipientEmail());

        String code = generateGiftCardCode();

        GiftCard card = GiftCard.builder()
                .code(code)
                .originalAmount(req.getAmount())
                .remainingBalance(req.getAmount())
                .status(GiftCard.GiftCardStatus.ACTIVE)
                .purchasedByCustomerId(req.getCustomerId())
                .recipientEmail(req.getRecipientEmail())
                .recipientName(req.getRecipientName())
                .message(req.getMessage())
                .expiresAt(LocalDateTime.now().plusYears(1))
                .build();

        card = giftCardRepo.save(card);
        eventProducer.publishGiftCardPurchased(code, req.getCustomerId(),
                req.getRecipientEmail(), req.getAmount());
        return toResponse(card);
    }

    // ── Get gift card balance ─────────────────────────────────────
    public GiftCardResponse getGiftCardBalance(String code) {
        GiftCard card = giftCardRepo.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Gift card not found: " + code));
        return toResponse(card);
    }

    // ── Redeem gift card against an order ─────────────────────────
    @Transactional
    public GiftCardResponse redeemGiftCard(GiftCardRedeemRequest req) {
        log.info("[GIFTCARD] Redeeming code={} orderId={} amount={}", req.getCode(), req.getOrderId(), req.getAmountToRedeem());

        GiftCard card = giftCardRepo.findByCode(req.getCode())
                .orElseThrow(() -> new RuntimeException("Gift card not found: " + req.getCode()));

        if (!card.isUsable()) {
            throw new RuntimeException("Gift card is not usable — status: " + card.getStatus() +
                    (card.isExpired() ? " (EXPIRED)" : ""));
        }

        double redeemAmount = Math.min(req.getAmountToRedeem(), card.getRemainingBalance());
        card.setRemainingBalance(card.getRemainingBalance() - redeemAmount);
        card.setLastUsedAt(LocalDateTime.now());

        if (card.getRemainingBalance() <= 0) {
            card.setStatus(GiftCard.GiftCardStatus.REDEEMED);
        }

        card = giftCardRepo.save(card);
        eventProducer.publishGiftCardRedeemed(req.getCode(), req.getCustomerId(),
                req.getOrderId(), redeemAmount, card.getRemainingBalance());
        return toResponse(card);
    }

    // ── Add store credit to customer ──────────────────────────────
    @Transactional
    public StoreCreditResponse addStoreCredit(String customerId, double amount, String reason) {
        log.info("[GIFTCARD] Adding store credit={}, reason={} to customer={}", amount, reason, customerId);

        StoreCredit credit = storeCreditRepo.findByCustomerId(customerId)
                .orElse(StoreCredit.builder().customerId(customerId).balance(0.0).build());
        credit.addCredit(amount);
        credit = storeCreditRepo.save(credit);
        eventProducer.publishCreditAdded(customerId, amount, reason);
        return toStoreCreditResponse(credit);
    }

    // ── Get store credit balance ──────────────────────────────────
    public StoreCreditResponse getStoreCredit(String customerId) {
        StoreCredit credit = storeCreditRepo.findByCustomerId(customerId)
                .orElse(StoreCredit.builder().customerId(customerId).balance(0.0).build());
        return toStoreCreditResponse(credit);
    }

    // ── Use store credit ──────────────────────────────────────────
    @Transactional
    public StoreCreditResponse useStoreCredit(String customerId, double amount) {
        StoreCredit credit = storeCreditRepo.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("No store credit found for: " + customerId));
        if (!credit.hasSufficientCredit(amount)) {
            throw new RuntimeException("Insufficient store credit. Available: " + credit.getBalance());
        }
        credit.setBalance(credit.getBalance() - amount);
        credit = storeCreditRepo.save(credit);
        log.info("[GIFTCARD] Used store credit={}, remaining={} for customer={}", amount, credit.getBalance(), customerId);
        return toStoreCreditResponse(credit);
    }

    // ── Get all gift cards for a customer ─────────────────────────
    public List<GiftCardResponse> getCustomerGiftCards(String customerId) {
        return giftCardRepo.findByPurchasedByCustomerId(customerId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private String generateGiftCardCode() {
        return "GIFT-" + Long.toHexString(System.currentTimeMillis()).toUpperCase()
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private GiftCardResponse toResponse(GiftCard c) {
        return GiftCardResponse.builder()
                .id(c.getId()).code(c.getCode())
                .originalAmount(c.getOriginalAmount()).remainingBalance(c.getRemainingBalance())
                .status(c.getStatus().name()).recipientEmail(c.getRecipientEmail())
                .recipientName(c.getRecipientName()).message(c.getMessage())
                .purchasedAt(c.getPurchasedAt()).expiresAt(c.getExpiresAt())
                .usable(c.isUsable()).build();
    }

    private StoreCreditResponse toStoreCreditResponse(StoreCredit s) {
        return StoreCreditResponse.builder()
                .customerId(s.getCustomerId()).balance(s.getBalance())
                .lastUpdated(s.getLastUpdated()).build();
    }
}
