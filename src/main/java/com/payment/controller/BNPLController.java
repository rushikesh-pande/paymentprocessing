package com.payment.controller;

import com.payment.dto.BNPLRequest;
import com.payment.model.BNPLTransaction;
import com.payment.service.BNPLService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bnpl")
@RequiredArgsConstructor
public class BNPLController {

    private final BNPLService bnplService;

    @PostMapping("/create")
    public ResponseEntity<BNPLTransaction> createBNPLTransaction(@Valid @RequestBody BNPLRequest request) {
        BNPLTransaction bnpl = bnplService.createBNPLTransaction(request);
        return new ResponseEntity<>(bnpl, HttpStatus.CREATED);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<BNPLTransaction> getBNPLByOrderId(@PathVariable String orderId) {
        BNPLTransaction bnpl = bnplService.getBNPLByOrderId(orderId);
        return ResponseEntity.ok(bnpl);
    }
}

