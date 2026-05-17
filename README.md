# Wallet Transaction System (Demo)

Lightweight Spring Boot demo project implementing a wallet/transaction system with JWT authentication.

## Overview

This project demonstrates basic user registration/login, wallet management, and transaction operations (deposit, withdraw, transfer) with standard HTTP API endpoints and JWT-based security.

## Tech stack

- Java 17
- Spring Boot 4.x
- Spring Security (JWT)
- Spring Data JPA
- PostgreSQL (runtime)
- Maven (wrapped)

## Project layout

- `demo/` - main Spring Boot application and sources

## Prerequisites

- Java 17 installed and JAVA_HOME set
- Docker (optional) or a running PostgreSQL instance
- Windows PowerShell (instructions below use PowerShell)

Default configuration (see `demo/src/main/resources/application.properties`):

- server port: 9699
- datasource jdbc url: `jdbc:postgresql://localhost:5452/my_wallet_transaction_db`
- username/password: `postgres` / `postgres`

You can start a Postgres docker container for local development:

```powershell
docker run --name my-postgres-wallettsms -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=my_wallet_transaction_db -p 5452:5432 -d postgres
```

## Build & Run (Windows PowerShell)

From project root run:

```powershell
cd demo
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

The application will start on http://localhost:9699

## Authentication

- POST /api/auth/login — returns a JWT token. Use the token in `Authorization: Bearer <token>` for protected endpoints.

Request example (JSON):

```json
{
  "email": "user@example.com",
  "password": "secret"
}
```

Response (200):

```json
{
  "token": "<jwt-token>",
  "role": "USER"
}
```

## Main API Endpoints

Users
- POST /api/users/register — register a new user (public)
  - body: `{ "email": "...", "password": "..." }`
  - response: 201 Created with user info

- PATCH /api/users/{userId}/role?role=ADMIN — assign role (requires authentication & proper privileges)

- GET /api/users?email={email} — find user by email

Wallets
- POST /api/wallet — create a wallet for the authenticated user
  - body: `{ "currency": "USD" }` (Currency enum) 
  - response: 201 Created

- GET /api/wallet/my — list wallets of the authenticated user

Transactions
- POST /api/transactions/wallets/{walletId}/deposit — create a deposit
  - body: `{ "amount": 100.00 }`

- POST /api/transactions/wallets/{walletId}/withdraw — create a withdrawal
  - body: `{ "amount": 50.00 }`

- POST /api/transactions/wallets/{walletId}/transfer — transfer from `{walletId}` to another wallet
  - body: `{ "amount": 10.00, "targetWalletId": "<uuid>" }`

- GET /api/transactions/wallets/{walletId} — list transactions for a wallet

- GET /api/transactions/status/{walletId}?status=COMPLETED — filter by `TransactionStatus` enum

- GET /api/transactions/type/{walletId}?type=TRANSFER — filter by `TransactionType` enum

Notes: All wallet/transaction operations require an Authorization header with a valid JWT.

## DTO shapes (examples)

- Register user: `{ "email": "a@b.com", "password": "pwd" }`
- Login: `{ "email": "a@b.com", "password": "pwd" }`
- Create wallet: `{ "currency": "USD" }`  (Currency enum is used in code)
- Deposit/Withdraw: `{ "amount": 100.00 }`
- Transfer: `{ "amount": 50.00, "targetWalletId": "<uuid>" }`

## Exception -> HTTP status mapping (recommended)

- BadRequestException -> 400 Bad Request
  - When the request is malformed or fails validation (missing/invalid fields)

- NotFoundException -> 404 Not Found
  - When a resource (user, wallet, transaction) is not found by id

- UnauthorizedException -> 401 Unauthorized
  - When authentication is missing/invalid (missing/expired JWT)

- ForbiddenException -> 403 Forbidden
  - When an authenticated user lacks permission to perform an action

- ConcurrencyException -> 409 Conflict
  - When optimistic locking/version conflicts occur (concurrent updates)

- BusinessException -> 422 Unprocessable Entity (or 400/409 depending on context)
  - When a business rule is violated (e.g., withdraw amount exceeds balance, transfer to same wallet, etc.)

The project contains a `GlobalExceptionHandler` which centralizes exception handling — follow the same mapping there for consistency.

## Tests

Use the maven wrapper to run tests:

```powershell
cd demo
.\mvnw.cmd test
```

## Useful tips

- Change `spring.private.token` in `application.properties` for a real secret in production.
- For development use an in-memory DB or Dockerized Postgres as documented above.
- Enable/debug logging with `logging.level.*` properties in `application.properties`.

## License

MIT-style / as appropriate for your use — adjust as needed.

---

If you want, I can also:
- Add example curl/Postman requests for all endpoints
- Create a small Postman collection and include it in the repo
- Add a health-check endpoint and README badge

Feel free to ask which of the above you'd like next.

