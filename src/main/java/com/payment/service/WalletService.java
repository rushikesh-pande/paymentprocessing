package com.payment.service;

import com.payment.dto.WalletRechargeRequest;
import com.payment.kafka.WalletEventProducer;
import com.payment.model.*;
import com.payment.repository.WalletRepository;
import com.payment.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final WalletEventProducer eventProducer;

    @Transactional
    public Wallet createWallet(String customerId) {
        if (walletRepository.existsByCustomerId(customerId)) {
            throw new IllegalStateException("Wallet already exists for customer: " + customerId);
        }

        Wallet wallet = new Wallet();
        wallet.setCustomerId(customerId);
        wallet.setBalance(0.0);
        wallet.setCurrency("USD");
        wallet.setStatus(WalletStatus.ACTIVE);

        Wallet savedWallet = walletRepository.save(wallet);
        log.info("Created wallet {} for customer {}", savedWallet.getWalletId(), customerId);

        return savedWallet;
    }

    @Transactional
    public Wallet rechargeWallet(WalletRechargeRequest request) {
        log.info("Processing wallet recharge for customer: {}", request.getCustomerId());

        Wallet wallet = walletRepository.findByCustomerId(request.getCustomerId())
            .orElseGet(() -> createWallet(request.getCustomerId()));

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Wallet is not active");
        }

        Double balanceBefore = wallet.getBalance();
        wallet.setBalance(wallet.getBalance() + request.getAmount());
        Wallet updatedWallet = walletRepository.save(wallet);

        // Record transaction
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletId(wallet.getWalletId());
        transaction.setCustomerId(request.getCustomerId());
        transaction.setType(WalletTransactionType.RECHARGE);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(wallet.getBalance());
        transaction.setDescription("Wallet recharge via " + request.getPaymentMethod());
        transaction.setStatus(WalletTransactionStatus.COMPLETED);
        transactionRepository.save(transaction);

        // Publish event
        eventProducer.publishWalletRecharged(wallet, request.getAmount());

        log.info("Wallet recharged successfully. New balance: {}", wallet.getBalance());

        return updatedWallet;
    }

    @Transactional
    public boolean debitWallet(String customerId, Double amount, String orderId, String description) {
        log.info("Debiting {} from wallet for customer: {}", amount, customerId);

        Wallet wallet = walletRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new RuntimeException("Wallet not found for customer: " + customerId));

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Wallet is not active");
        }

        if (wallet.getBalance() < amount) {
            log.warn("Insufficient balance. Required: {}, Available: {}", amount, wallet.getBalance());
            return false;
        }

        Double balanceBefore = wallet.getBalance();
        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);

        // Record transaction
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletId(wallet.getWalletId());
        transaction.setCustomerId(customerId);
        transaction.setType(WalletTransactionType.PAYMENT);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(wallet.getBalance());
        transaction.setOrderId(orderId);
        transaction.setDescription(description != null ? description : "Payment for order");
        transaction.setStatus(WalletTransactionStatus.COMPLETED);
        transactionRepository.save(transaction);

        log.info("Wallet debited successfully. New balance: {}", wallet.getBalance());

        return true;
    }

    public Wallet getWalletByCustomerId(String customerId) {
        return walletRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new RuntimeException("Wallet not found for customer: " + customerId));
    }

    public Double getWalletBalance(String customerId) {
        Wallet wallet = getWalletByCustomerId(customerId);
        return wallet.getBalance();
    }

    public List<WalletTransaction> getTransactionHistory(String customerId) {
        return transactionRepository.findByCustomerId(customerId);
    }
}

