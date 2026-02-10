# Payment Processing Service

## Overview
Microservice for processing customer order payments with multiple payment methods.

## Features
- Process payments (Credit Card, Debit Card, UPI, Wallet, COD)
- Payment gateway integration
- Transaction tracking
- Payment status retrieval

## API Endpoints

### Process Payment
**POST** `/api/v1/payments`

### Get Payment Status
**GET** `/api/v1/payments/{paymentId}`

## Technology Stack
- Java 17
- Spring Boot 3.2.2
- PostgreSQL

## Running
```bash
mvn spring-boot:run
```

