package com.payment.controller;

import com.payment.dto.EMIRequest;
import com.payment.model.EMIPlan;
import com.payment.service.EMIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/emi")
@RequiredArgsConstructor
public class EMIController {

    private final EMIService emiService;

    @PostMapping("/create")
    public ResponseEntity<EMIPlan> createEMIPlan(@Valid @RequestBody EMIRequest request) {
        EMIPlan emiPlan = emiService.createEMIPlan(request);
        return new ResponseEntity<>(emiPlan, HttpStatus.CREATED);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<EMIPlan> getEMIPlanByOrderId(@PathVariable String orderId) {
        EMIPlan emiPlan = emiService.getEMIPlanByOrderId(orderId);
        return ResponseEntity.ok(emiPlan);
    }
}

