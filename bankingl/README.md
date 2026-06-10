# Banking System - Spring Boot

JWT Security aur Loan Management ke saath ek complete banking REST API.

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT (JJWT 0.11.5)
- Spring Data JPA
- H2 In-Memory Database
- Lombok
- Bean Validation

## Project Structure

```
src/main/java/com/banking/
├── BankingSystemApplication.java
├── config/
│   ├── SecurityConfig.java         # JWT security configuration
│   └── DataInitializer.java        # Test data seed
├── controller/
│   ├── AuthController.java         # /api/auth/**
│   ├── LoanController.java         # /api/loans/** (customer)
│   └── AdminController.java        # /api/admin/** (admin only)
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   ├── LoanApplicationRequest.java
│   ├── LoanStatusUpdateRequest.java
│   ├── LoanPaymentRequest.java
│   ├── LoanResponse.java
│   └── ApiResponse.java
├── entity/
│   ├── User.java
│   ├── Loan.java
│   └── LoanPayment.java
├── enums/
│   ├── Role.java                   # ROLE_CUSTOMER, ROLE_ADMIN
│   ├── LoanStatus.java             # PENDING, ACTIVE, CLOSED, REJECTED
│   └── LoanType.java               # PERSONAL, HOME, CAR, BUSINESS, EDUCATION
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── BusinessException.java
│   ├── DuplicateResourceException.java
│   └── GlobalExceptionHandler.java
├── repository/
│   ├── UserRepository.java
│   ├── LoanRepository.java
│   └── LoanPaymentRepository.java
├── security/
│   ├── JwtService.java             # Token generate/validate
│   └── JwtAuthenticationFilter.java
└── service/
    ├── AuthService.java
    └── LoanService.java            # EMI calculation logic
```

## Setup & Run

### Requirements
- Java 17+
- Maven 3.8+

### Steps

```bash
# 1. Project folder mein jao
cd banking-system

# 2. Build karo
mvn clean install

# 3. Run karo
mvn spring-boot:run
```

Server `http://localhost:8080` par start hoga.

### H2 Console
Browser mein: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:bankingdb`
- Username: `sa`
- Password: (empty)

---

## API Endpoints

### Auth (Public - No Token Needed)

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/register` | Naya customer register |
| POST | `/api/auth/login` | Login aur JWT token lena |
| POST | `/api/auth/register-admin` | Admin register |

### Loans (Customer - Token Required)

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/loans/apply` | Loan apply karna |
| GET | `/api/loans/my-loans` | Apni loans dekhna |
| GET | `/api/loans/{id}` | Single loan detail |
| POST | `/api/loans/{id}/pay` | EMI payment karna |

### Admin (Admin Role Required)

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/admin/loans` | Saari loans |
| GET | `/api/admin/loans/pending` | Pending loans |
| PUT | `/api/admin/loans/{id}/status` | Approve/Reject |

---

## Postman Testing Guide

### Step 1: Login

```json
POST http://localhost:8080/api/auth/login
{
    "email": "customer@bank.com",
    "password": "Customer@1234"
}
```

Response mein token milega. Usse copy karo.

### Step 2: Token Set Karo

Postman mein Authorization tab > Bearer Token > Token paste karo.

### Step 3: Loan Apply Karo

```json
POST http://localhost:8080/api/loans/apply
Authorization: Bearer {your_token}

{
    "loanType": "PERSONAL",
    "principalAmount": 500000.00,
    "tenureMonths": 24,
    "purpose": "Ghar ki renovation ke liye"
}
```

### Step 4: Admin se Approve Karwao

```json
POST http://localhost:8080/api/auth/login
{
    "email": "admin@bank.com",
    "password": "Admin@1234"
}
```

```json
PUT http://localhost:8080/api/admin/loans/1/status
Authorization: Bearer {admin_token}

{
    "status": "APPROVED",
    "adminRemarks": "Loan approved after verification"
}
```

### Step 5: Payment Karo

```json
POST http://localhost:8080/api/loans/1/pay
Authorization: Bearer {customer_token}

{
    "amountPaid": 25000.00
}
```

---

## Interest Rates

| Loan Type | Annual Rate |
|-----------|-------------|
| PERSONAL | 14% |
| HOME | 10% |
| CAR | 12% |
| BUSINESS | 13.5% |
| EDUCATION | 8% |

## EMI Formula

```
EMI = P × r × (1+r)^n / ((1+r)^n - 1)

P = Principal Amount
r = Monthly Interest Rate (Annual Rate / 12 / 100)
n = Tenure in Months
```

## Default Test Users

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@bank.com | Admin@1234 |
| Customer | customer@bank.com | Customer@1234 |

---

## Validation Rules

- **CNIC**: Format `12345-1234567-1`
- **Phone**: Format `+923001234567`
- **Password**: Min 8 characters
- **Loan Amount**: 10,000 se 10,000,000 tak
- **Tenure**: 3 se 360 months tak
      




       THANK YOU            {NOOR AHMED }