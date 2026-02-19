package com.payment.model;

public enum WalletTransactionType {
    CREDIT,          // Money added to wallet
    DEBIT,           // Money deducted from wallet
    RECHARGE,        // Wallet recharge
    REFUND,          // Refund to wallet
    PAYMENT,         // Payment from wallet
    CASHBACK,        // Cashback credited
    BONUS,           // Bonus amount
    TRANSFER_IN,     // Money transferred in
    TRANSFER_OUT,    // Money transferred out
    REVERSAL         // Transaction reversal
}

