package com.payment.repository;

import com.payment.model.Wallet;
import com.payment.model.WalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByCustomerId(String customerId);

    Optional<Wallet> findByWalletId(String walletId);

    boolean existsByCustomerId(String customerId);

    Optional<Wallet> findByCustomerIdAndStatus(String customerId, WalletStatus status);
}

