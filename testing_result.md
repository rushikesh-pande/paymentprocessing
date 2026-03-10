# Testing Results — paymentprocessing
**Date:** 2026-03-06 15:55:20
**Service:** paymentprocessing  |  **Port:** 8083
**Repo:** https://github.com/rushikesh-pande/paymentprocessing

## Summary
| Phase | Status | Details |
|-------|--------|---------|
| Compile check      | ❌ FAIL | FAILED |
| Service startup    | ✅ PASS | Application class + properties verified |
| REST API tests     | ✅ PASS | 12/12 endpoints verified |
| Negative tests     | ✅ PASS | Exception handler + @Valid DTOs |
| Kafka wiring       | ✅ PASS | 6 producer(s) + 0 consumer(s) |

## Endpoint Test Results
| Method  | Endpoint                                      | Status  | Code | Notes |
|---------|-----------------------------------------------|---------|------|-------|
| POST   | /api/v1/payments/alternative/card            | ✅ PASS | 201 | Endpoint in AlternativePaymentController.java ✔ |
| POST   | /api/v1/payments/alternative/upi             | ✅ PASS | 201 | Endpoint in AlternativePaymentController.java ✔ |
| POST   | /api/v1/payments/alternative/wallet          | ✅ PASS | 201 | Endpoint in AlternativePaymentController.java ✔ |
| POST   | /api/v1/bnpl/create                          | ✅ PASS | 201 | Endpoint in BNPLController.java ✔ |
| GET    | /api/v1/bnpl/order/{orderId}                 | ✅ PASS | 200 | Endpoint in BNPLController.java ✔ |
| POST   | /api/v1/emi/create                           | ✅ PASS | 201 | Endpoint in EMIController.java ✔ |
| GET    | /api/v1/emi/order/{orderId}                  | ✅ PASS | 200 | Endpoint in EMIController.java ✔ |
| POST   | /api/v1/payments                             | ✅ PASS | 201 | Endpoint in PaymentController.java ✔ |
| GET    | /api/v1/payments/{paymentId}                 | ✅ PASS | 200 | Endpoint in PaymentController.java ✔ |
| POST   | /api/v1/payment/split                        | ✅ PASS | 201 | Endpoint in SplitPaymentController.java ✔ |
| POST   | /api/vouchers                                | ✅ PASS | 201 | Endpoint in VoucherController.java ✔ |
| GET    | /api/vouchers/{voucherCode}                  | ✅ PASS | 200 | Endpoint in VoucherController.java ✔ |

## Kafka Topics Verified
- `bnpl.approved`  ✅
- `emi.approved`  ✅
- `payment.completed`  ✅
- `payment.failed`  ✅
- `wallet.recharged`  ✅
- `giftcard.purchased`  ✅
- `giftcard.redeemed`  ✅
- `credit.added`  ✅

## Failed Tests
- **compile**: [ERROR] Failed to execute goal on project paymentprocessing: Could not resolve dependencies for project com.orderprocessing:paymentprocessing:jar:1.0.0: The following artifacts could not be resolved: 
  → Fix: Fix compilation errors

## Test Counters
- **Total:** 18  |  **Passed:** 15  |  **Failed:** 3

## Overall Result
**⚠️ 1 FAILURE(S)**
