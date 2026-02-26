package com.payment.giftcard.repository;

import com.payment.giftcard.entity.GiftCard;
import com.payment.giftcard.entity.GiftCard.GiftCardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Enhancement #14 - Gift Card JPA Repository.
 */
@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard, Long> {
    Optional<GiftCard> findByCode(String code);
    List<GiftCard> findByPurchasedByCustomerId(String customerId);
    List<GiftCard> findByRecipientEmail(String email);
    List<GiftCard> findByStatus(GiftCardStatus status);
}
