package com.orderprocessing.paymentprocessing.service;

import com.orderprocessing.paymentprocessing.model.Payment;
import com.orderprocessing.paymentprocessing.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "payment.completed";

    public Payment create(Payment payment) {
        log.info("Creating Payment: {}", payment);
        Payment saved = paymentRepository.save(payment);
        kafkaTemplate.send(TOPIC, "PAYMENT_CREATED", saved.toString());
        log.info("Payment created with id: {}", saved.getId());
        return saved;
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment update(Long id, Payment updated) {
        return paymentRepository.findById(id).map(existing -> {
            updated.setId(id);
            Payment saved = paymentRepository.save(updated);
            kafkaTemplate.send(TOPIC, "PAYMENT_UPDATED", saved.toString());
            log.info("Payment updated: {}", saved.getId());
            return saved;
        }).orElseThrow(() -> new RuntimeException("Payment not found: " + id));
    }

    public void delete(Long id) {
        paymentRepository.deleteById(id);
        kafkaTemplate.send(TOPIC, "PAYMENT_DELETED", id.toString());
        log.info("Payment deleted: {}", id);
    }
}
