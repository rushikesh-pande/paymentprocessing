package com.payment.giftcard.repository;

import com.payment.giftcard.entity.StoreCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Enhancement #14 - Store Credit JPA Repository.
 */
@Repository
public interface StoreCreditRepository extends JpaRepository<StoreCredit, Long> {
    Optional<StoreCredit> findByCustomerId(String customerId);
}
