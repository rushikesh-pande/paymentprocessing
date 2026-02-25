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
