package com.payment.repository;

import com.payment.model.BNPLTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BNPLTransactionRepository extends JpaRepository<BNPLTransaction, Long> {

    Optional<BNPLTransaction> findByBnplId(String bnplId);

    List<BNPLTransaction> findByCustomerId(String customerId);

    Optional<BNPLTransaction> findByOrderId(String orderId);
}

