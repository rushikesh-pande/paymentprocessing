package com.payment.repository;

import com.payment.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByCustomerId(String customerId);

    List<WalletTransaction> findByWalletId(String walletId);

    List<WalletTransaction> findByOrderId(String orderId);
}

