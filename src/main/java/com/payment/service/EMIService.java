package com.payment.service;

import com.payment.dto.EMIRequest;
import com.payment.kafka.EMIEventProducer;
import com.payment.model.EMIPlan;
import com.payment.model.EMIStatus;
import com.payment.repository.EMIPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EMIService {

    private final EMIPlanRepository emiPlanRepository;
    private final EMIEventProducer eventProducer;

    @Transactional
    public EMIPlan createEMIPlan(EMIRequest request) {
        log.info("Creating EMI plan for order: {}", request.getOrderId());

        // Calculate monthly installment
        Double totalWithInterest = request.getAmount() * (1 + (request.getInterestRate() / 100));
        Double monthlyInstallment = totalWithInterest / request.getTenureMonths();

        EMIPlan emiPlan = new EMIPlan();
        emiPlan.setOrderId(request.getOrderId());
        emiPlan.setCustomerId(request.getCustomerId());
        emiPlan.setTotalAmount(request.getAmount());
        emiPlan.setTenureMonths(request.getTenureMonths());
        emiPlan.setMonthlyInstallment(monthlyInstallment);
        emiPlan.setInterestRate(request.getInterestRate());
        emiPlan.setRemainingInstallments(request.getTenureMonths());
        emiPlan.setStatus(EMIStatus.ACTIVE);
        emiPlan.setStartDate(LocalDateTime.now());
        emiPlan.setNextDueDate(LocalDateTime.now().plusMonths(1));

        EMIPlan savedPlan = emiPlanRepository.save(emiPlan);

        // Publish event
        eventProducer.publishEMIApproved(savedPlan);

        log.info("EMI plan created: {} with {} monthly installments of {}", 
            savedPlan.getEmiPlanId(), request.getTenureMonths(), monthlyInstallment);

        return savedPlan;
    }

    public EMIPlan getEMIPlanByOrderId(String orderId) {
        return emiPlanRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("EMI plan not found for order: " + orderId));
    }
}

