package com.payment.repository;

import com.payment.model.EMIPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EMIPlanRepository extends JpaRepository<EMIPlan, Long> {

    Optional<EMIPlan> findByEmiPlanId(String emiPlanId);

    List<EMIPlan> findByCustomerId(String customerId);

    Optional<EMIPlan> findByOrderId(String orderId);
}

