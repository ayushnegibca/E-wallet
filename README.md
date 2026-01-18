# E-wallet

E-wallet is a Java Spring Boot microservice for managing user wallets, balances, and peer-to-peer transfers. This project uses JWT for session management, Redis to store OTPs, MySQL as the primary persistent store, and Kafka for asynchronous events.

## Project summary

- Purpose: Provide REST APIs to register/verify users (OTP), manage wallets (deposit/withdraw/transfer), and record transactions.
- Architecture: Spring Boot microservice(s) + MySQL (persistent) + Redis (OTP storage) + Kafka (events). JWT tokens used for session management.

## Tech stack

- Java + Spring Boot
- MySQL (persistent storage)
- Redis (OTP storage, optional locks)
- Kafka (transactions, notifications)
- JWT for access tokens (session management)
- Flyway or Liquibase for DB migrations

## High-level architecture

- REST API (Spring Boot controllers) handles requests.
- MySQL stores Users, Wallets, Transactions, Ledger entries.
- Redis stores one-time passcodes (OTPs) with short TTL and may be used for ephemeral locks.
- JWTs are issued after successful OTP verification and used for subsequent requests as Authorization: Bearer <token>.
- Kafka producers publish transaction events (deposit/withdraw/transfer); consumers perform asynchronous processing (notifications, audit, reconciliation).

## Auth & OTP flow (implementation notes)

1) Request OTP
- POST /auth/request-otp
- Body: { "phone": "+123..." } or { "email": "user@example.com" }
- Action: generate numeric OTP (6 digits), store in Redis with key `otp:<recipient>` or `otp:user:<userId>` and TTL (e.g., 5m), send via SMS/email.
- Response: 202 Accepted

2) Verify OTP and issue JWT
- POST /auth/verify-otp
- Body: { "recipient": "+123..." or "userId": "...", "otp": "123456" }
- Action: validate against Redis entry, on success create or retrieve user record, issue JWT access token (and optionally a refresh token if you implement one).
- Response 200: { "accessToken": "<jwt>", "tokenType": "Bearer", "expiresIn": 900 }

Notes on JWT session management
- JWT used for session management (access token). Keep access tokens short-lived (e.g., 15 minutes) to limit risk.
- If you implement refresh tokens, store them securely (MySQL or Redis) and rotate them on use.
- For logout/revocation, you can store revoked JWT ids (jti) in Redis until the token expiry.

## API endpoints

All wallet/transaction endpoints are protected and require Authorization: Bearer <accessToken>.

- POST /auth/request-otp
  - Request OTP to a phone/email. 202 Accepted.

- POST /auth/verify-otp
  - Verify OTP and return access token. 200 { accessToken, expiresIn }

- GET /users/{id}
  - Get user profile (id, name, email/phone). 200

- GET /wallets/{userId}
  - Get wallet balance. 200 { userId, balance }

- POST /wallets/deposit
  - Body: { "userId": "...", "amount": number, "source": "string" }
  - Action: create deposit transaction, update balance, produce Kafka `transactions` event. 200 { transactionId, newBalance }

- POST /wallets/withdraw
  - Body: { "userId": "...", "amount": number }
  - Action: check balance, create transaction, update balance, produce event. 200 { transactionId, newBalance }
  - Error: 422 Insufficient funds

- POST /wallets/transfer
  - Body: { "fromUserId": "...", "toUserId": "...", "amount": number }
  - Action: perform atomic DB transaction to debit/credit wallets, create ledger entries for both sides, produce `transactions` event. 200 { transactionId, fromNewBalance, toNewBalance }

- GET /transactions
  - Query: ?limit=&offset=&type=deposit|withdraw|transfer
  - List transactions for authenticated user. 200 [ ... ]

- GET /transactions/{id}
  - Transaction details. 200 { id, type, amount, from, to, timestamp }

## Redis usage

- Store OTPs: key pattern `otp:<recipient>` or `otp:user:<userId>`, TTL e.g., 300 seconds.
- Optional: short locks `lock:wallet:<userId>` during transfers to reduce contention.
- Optional: store blacklisted JWT jti values for token revocation until expiry.

## Kafka usage (recommended defaults)

- transactions — produced on deposit/withdraw/transfer (payload includes transaction id, type, amount, from/to, timestamp)
- notifications — produced for user-facing notifications (email/SMS)
- audit — optional topic for audit/reconciliation

Adjust topic names to your production naming conventions.

## MySQL schema suggestions (simplified)

- users (id PK, name, email, phone, created_at)
- wallets (id PK, user_id FK, balance DECIMAL(18,2), currency, updated_at)
- transactions (id PK, type ENUM, amount DECIMAL(18,2), from_user_id, to_user_id, status, metadata JSON, created_at)
- ledger_entries (id PK, transaction_id FK, user_id, amount, balance_after, created_at)

Use appropriate indexes and foreign keys for consistency. Use DECIMAL for monetary values and avoid floating point.

## Concurrency & safety

- Use database transactions when updating balances and creating transaction/ledger records.
- Use row-level locking or optimistic locking (version columns) to prevent race conditions.
- Optionally acquire a short Redis lock (SETNX with TTL) per wallet for high contention scenarios.

## Spring Boot notes & example configuration

Dependencies (Maven coordinates / starters):
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-starter-data-jpa
- org.springframework.boot:spring-boot-starter-data-redis
- org.springframework.kafka:spring-kafka
- io.jsonwebtoken:jjwt (or use Spring Security OAuth2 Resource Server JWT)
- mysql:mysql-connector-java
- org.flywaydb:flyway-core (or liquibase)

Example application.properties (or application.yml) skeleton:

spring.datasource.url=jdbc:mysql://localhost:3306/ewallet
spring.datasource.username=youruser
spring.datasource.password=yourpass
spring.jpa.hibernate.ddl-auto=validate

spring.redis.host=localhost
spring.redis.port=6379

spring.kafka.bootstrap-servers=localhost:9092

app.jwt.secret=your_jwt_secret_here
app.jwt.expiration-seconds=900
app.otp.ttl-seconds=300

Notes:
- Use environment variables or externalized configuration for secrets and connection strings.
- Use Flyway/Liquibase for production DB migrations.

## Example flows (curl)

Request OTP:
curl -X POST https://api.example.com/auth/request-otp \
  -H 'Content-Type: application/json' \
  -d '{"phone":"+1234567890"}'

Verify OTP (get token):
curl -X POST https://api.example.com/auth/verify-otp \
  -H 'Content-Type: application/json' \
  -d '{"recipient":"+1234567890","otp":"123456"}'

Transfer (authenticated):
curl -X POST https://api.example.com/wallets/transfer \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <access_token>' \
  -d '{"fromUserId":"u1","toUserId":"u2","amount":50}'

## Errors and status codes

- 200 OK — success
- 201 Created — resource created
- 202 Accepted — OTP requested
- 400 Bad Request — invalid input
- 401 Unauthorized — missing/invalid token
- 403 Forbidden — not allowed
- 404 Not Found — resource missing
- 409 Conflict — concurrency conflict
- 422 Unprocessable Entity — business rules (e.g., insufficient funds)

## Running locally (recommended)

- Start MySQL, Redis, and Kafka (use docker-compose for convenience).
- Configure application.properties with connection settings and JWT secret.
- Run: ./mvnw spring-boot:run

## Contributing

Pull requests welcome. Please include tests for critical paths (transfers, balance updates) and ensure Flyway migrations are included.

---

Update made on user's request to reflect Spring Boot, JWT session management, Redis for OTP, MySQL, and Kafka. Please review and tell me if you want additional code examples (Spring Security filter, JWT utils, or controller templates) and I will add them.