package com.payment.controller;

import com.payment.dto.WalletRechargeRequest;
import com.payment.model.Wallet;
import com.payment.model.WalletTransaction;
import com.payment.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(@RequestParam String customerId) {
        Wallet wallet = walletService.createWallet(customerId);
        return new ResponseEntity<>(wallet, HttpStatus.CREATED);
    }

    @PostMapping("/recharge")
    public ResponseEntity<Wallet> rechargeWallet(@Valid @RequestBody WalletRechargeRequest request) {
        Wallet wallet = walletService.rechargeWallet(request);
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/balance/{customerId}")
    public ResponseEntity<Double> getBalance(@PathVariable String customerId) {
        Double balance = walletService.getWalletBalance(customerId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable String customerId) {
        Wallet wallet = walletService.getWalletByCustomerId(customerId);
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/transactions/{customerId}")
    public ResponseEntity<List<WalletTransaction>> getTransactions(@PathVariable String customerId) {
        List<WalletTransaction> transactions = walletService.getTransactionHistory(customerId);
        return ResponseEntity.ok(transactions);
    }
}

