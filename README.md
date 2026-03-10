# paymentprocessing

## Overview
Microservice for: **payment :- enchancement payement system to get voucher disscount if customer enters voucher code,publish event in kafka topic**

## Tech Stack
- Java 17
- Spring Boot 3.2.2
- Maven
- Kafka (topic: `payment.completed`)

## API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| POST   | /api/v1/payments | Create |
| GET    | /api/v1/payments | List all |
| GET    | /api/v1/payments/{id} | Get by ID |
| PUT    | /api/v1/payments/{id} | Update |
| DELETE | /api/v1/payments/{id} | Delete |

## Running
```bash
mvn spring-boot:run
```
Service runs on port **8083**

## Kafka
Topic: `payment.completed`
Events: `PAYMENT_CREATED`, `PAYMENT_UPDATED`, `PAYMENT_DELETED`

## 🔒 Security Enhancements

This service implements all 7 security enhancements:

| # | Enhancement | Implementation |
|---|-------------|----------------|
| 1 | **OAuth 2.0 / JWT** | `SecurityConfig.java` — stateless JWT auth, Bearer token validation |
| 2 | **API Rate Limiting** | `RateLimitingFilter.java` — 100 req/min per IP using Bucket4j |
| 3 | **Input Validation** | `InputSanitizer.java` — SQL injection, XSS, command injection prevention |
| 4 | **Data Encryption** | `EncryptionService.java` — AES-256-GCM for sensitive data at rest |
| 5 | **PCI DSS** | `PciDssAuditAspect.java` — Full audit trail for payment operations |
| 6 | **GDPR Compliance** | `GdprDataService.java` — Right to erasure, consent management, data export |
| 7 | **Audit Logging** | `AuditLogService.java` — All transactions logged with user, IP, timestamp |

### Security Endpoints
- `GET /api/v1/audit/recent?limit=100` — Recent audit events (ADMIN only)
- `GET /api/v1/audit/user/{userId}` — User's audit trail (ADMIN or self)
- `GET /api/v1/audit/violations` — Security violations (ADMIN only)

### JWT Authentication
```bash
# Include Bearer token in all requests:
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8083/api/v1/...
```

### Security Headers Added
- `X-Frame-Options: DENY`
- `X-Content-Type-Options: nosniff`
- `Strict-Transport-Security: max-age=31536000; includeSubDomains`
- `Referrer-Policy: strict-origin-when-cross-origin`
- `X-RateLimit-Remaining: <n>` (on every response)
