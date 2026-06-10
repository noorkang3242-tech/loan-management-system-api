# 💰 Loan Management System — Secure Banking REST API

> A secure, role-based **Loan Management REST API** built with **Spring Boot 3 + Spring Security + JWT**. Customers can register, apply for loans, and make repayments; admins review, approve/reject, and track loans.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-blue)
![Database](https://img.shields.io/badge/MySQL-8-blue)
![Build](https://img.shields.io/badge/Build-Maven-red)

---

## 📖 Overview

A backend REST API for a loan management system. It demonstrates **JWT-based authentication**, **role-based authorization** (CUSTOMER / ADMIN), input validation, a clean layered architecture (controller → service → repository), centralized exception handling, and a consistent API response envelope.

---

## ✨ Features

- 🔐 **JWT authentication** (register / login) with BCrypt-hashed passwords
- 👥 **Role-based access** — CUSTOMER and ADMIN, enforced with `@PreAuthorize`
- 🧾 **Loan lifecycle** — apply → admin approve/reject → active → repay → closed
- 💳 **Repayments** — record EMI / partial payments against a loan
- ✅ **Validation** — request DTOs validated (e.g. CNIC & phone format, amount/tenure ranges)
- 🧱 **Clean architecture** — DTOs, entities, repositories, services, custom exceptions
- 📦 **Consistent responses** — every endpoint returns a standard `ApiResponse` envelope

---

## 🛠️ Tech Stack

**Java 17** · **Spring Boot 3.2.0** · Spring Web · **Spring Security** · **JWT** (JJWT 0.11.5) · Spring Data JPA / Hibernate · **MySQL 8** · Bean Validation · Lombok · Maven.

---

## 🗂️ Project Structure

```
loan-management-system-api/
└── bankingl/                       # Maven project root
    ├── pom.xml
    └── src/main/java/com/banking/
        ├── controller/   # AuthController, LoanController, AdminController
        ├── service/      # AuthService, LoanService
        ├── repository/   # User, Loan, LoanPayment repositories
        ├── entity/       # User, Loan, LoanPayment
        ├── dto/          # request/response objects + ApiResponse
        ├── enums/        # Role, LoanType, LoanStatus
        ├── security/     # JwtService, JwtAuthenticationFilter
        ├── config/       # SecurityConfig, DataInitializer
        └── exception/    # GlobalExceptionHandler + custom exceptions
```

> Note: the Spring Boot application lives in the **`bankingl/`** sub-folder — run Maven commands from there.

---

## 📡 API Endpoints

### 🔑 Auth — `/api/auth`
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/register` | Public | Register a new customer, returns JWT |
| POST | `/login` | Public | Log in, returns JWT |
| POST | `/register-admin` | Public (dev only) | Create an admin user |

### 💸 Loans — `/api/loans` (authenticated customer)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/apply` | Apply for a loan |
| GET | `/my-loans` | List my loans |
| GET | `/{id}` | Get one loan |
| POST | `/{id}/pay` | Make a repayment |

### 🛡️ Admin — `/api/admin` (ROLE_ADMIN)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/loans` | List all loans |
| GET | `/loans/pending` | List loans pending approval |
| PUT | `/loans/{id}/status` | Approve / reject a loan |

**Enums:** `LoanType` = PERSONAL, HOME, CAR, BUSINESS, EDUCATION · `LoanStatus` = PENDING, APPROVED, REJECTED, ACTIVE, CLOSED.

---

## 📥 Example Requests

**Register**
```json
POST /api/auth/register
{
  "fullName": "Ali Khan",
  "email": "ali@example.com",
  "password": "secret123",
  "cnic": "12345-1234567-1",
  "phoneNumber": "+923001234567"
}
```

**Apply for a loan** (send `Authorization: Bearer <token>`)
```json
POST /api/loans/apply
{
  "loanType": "PERSONAL",
  "principalAmount": 50000,
  "tenureMonths": 12,
  "purpose": "Home renovation"
}
```

**Make a payment**
```json
POST /api/loans/{id}/pay
{ "amountPaid": 5000 }
```

**Admin update status**
```json
PUT /api/admin/loans/{id}/status
{ "status": "APPROVED", "adminRemarks": "Verified" }
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17, Maven 3.9+, MySQL 8 running on `localhost:3306`
- Create a database (e.g. `mydb`) — Hibernate creates the tables (`ddl-auto=update`)

### Configuration (set secrets via environment — never commit real values)
The app reads these from the environment (with local-dev fallbacks in `application.properties`):
```bash
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
DB_NAME=mydb
JWT_SECRET=a-long-random-secret-key-for-jwt-signing
```

### Run
```bash
cd bankingl
mvn spring-boot:run
```
The API starts on **http://localhost:8080**.

---

## 🔒 Security Notes

- Passwords are stored as **BCrypt** hashes.
- All `/api/loans/**` and `/api/admin/**` routes require a valid JWT; admin routes additionally require `ROLE_ADMIN`.
- **Secrets are environment-driven** — the committed `application.properties` contains only placeholders.

---

## 👤 Author

Built by **noorkang3242-tech** — a portfolio project demonstrating Spring Boot, Spring Security/JWT, and clean REST API design.
