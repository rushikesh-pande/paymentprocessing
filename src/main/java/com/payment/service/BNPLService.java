package com.payment.service;

import com.payment.dto.BNPLRequest;
import com.payment.kafka.BNPLEventProducer;
import com.payment.model.BNPLStatus;
import com.payment.model.BNPLTransaction;
import com.payment.repository.BNPLTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BNPLService {

    private final BNPLTransactionRepository bnplRepository;
    private final BNPLEventProducer eventProducer;

    @Transactional
    public BNPLTransaction createBNPLTransaction(BNPLRequest request) {
        log.info("Creating BNPL transaction for order: {} with provider: {}", 
            request.getOrderId(), request.getProvider());

        Double installmentAmount = request.getAmount() / request.getInstallments();

        BNPLTransaction bnpl = new BNPLTransaction();
        bnpl.setOrderId(request.getOrderId());
        bnpl.setCustomerId(request.getCustomerId());
        bnpl.setAmount(request.getAmount());
        bnpl.setProvider(request.getProvider());
        bnpl.setInstallments(request.getInstallments());
        bnpl.setInstallmentAmount(installmentAmount);
        bnpl.setStatus(BNPLStatus.APPROVED);
        bnpl.setDueDate(LocalDateTime.now().plusMonths(1));

        BNPLTransaction savedTransaction = bnplRepository.save(bnpl);

        // Publish event
        eventProducer.publishBNPLApproved(savedTransaction);

        log.info("BNPL transaction created: {} with {} installments of {}", 
            savedTransaction.getBnplId(), request.getInstallments(), installmentAmount);

        return savedTransaction;
    }

    public BNPLTransaction getBNPLByOrderId(String orderId) {
        return bnplRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("BNPL transaction not found for order: " + orderId));
    }
}

