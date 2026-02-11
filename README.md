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

### Alternative Payment Methods (Enhancement)

#### Card Payment
**POST** `/api/v1/payments/alternative/card`

Request body example:
```json
{
  "orderId": "ORD-A1B2C3D4",
  "amount": 1059.97,
  "cardType": "CREDIT",
  "cardNumber": "4111111111111111",
  "cardHolderName": "John Doe",
  "expiryMonth": 12,
  "expiryYear": 2028,
  "cvv": "123"
}
```

#### UPI Payment
**POST** `/api/v1/payments/alternative/upi`

Request body example:
```json
{
  "orderId": "ORD-A1B2C3D4",
  "amount": 1059.97,
  "upiId": "john.doe@okbank",
  "customerName": "John Doe",
  "customerPhone": "+15551234567"
}
```

## Technology Stack
- Java 17
- Spring Boot 3.2.2
- PostgreSQL

## Running
```bash
mvn spring-boot:run
```
