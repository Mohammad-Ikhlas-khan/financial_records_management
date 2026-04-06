# Financial Records Management API

A secure, role-based **RESTful API** built with **Java Spring Boot** for managing financial records. The system supports full CRUD operations on financial transactions, a financial dashboard with summary analytics, and JWT-based authentication with three distinct user roles.

---

## Tech Stack

| Layer       | Technology                   |
|-------------|------------------------------|
| Language    | Java 21+                     |
| Framework   | Spring Boot 3.x              |
| Security    | Spring Security + JWT (JJWT) |
| Build Tool  | Maven                        |
| Database    | PostgreSQL                   |
| ORM         | Spring Data JPA / Hibernate  |
| Validation  | Jakarta Bean Validation      |
| Boilerplate | Lombok                       |
| IDE         | IntelliJ IDEA                |

---


## Setup & Installation


### 1. Clone the Repository

```bash
git clone https://github.com/Mohammad-Ikhlas-khan/financial_records_management.git
cd financial_records_management/finance
```

### 2. Create the PostgreSQL Database

Connect to your PostgreSQL instance and run:

```sql
CREATE DATABASE your_db;
```

### 3. Configure `application.properties`

Edit `src/main/resources/application.properties`:

```properties
# PostgreSQL datasource
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
# JWT secret — use a strong random string, minimum 32 characters
jwt.secret=your_super_secret_key_at_least_32_characters_long

# Server port (optional, default is 8080)
server.port=8080
```

### 4. Build the Project

```bash
mvn clean install
```

---

## Running the Application

```bash
mvn spring-boot:run
```

The server starts at: **`http://localhost:8080`**

---

## Default Admin Credentials

On first startup, `DataInitializer` automatically seeds a default admin account if none exists:

| Field    | Value      |
|----------|------------|
| Username | `admin`    |
| Password | `admin123` |
| Role     | `ADMIN`    |

> ⚠️ **Change the default password immediately in any non-local environment.**

---

## Authentication

This API uses **stateless JWT authentication**. Every protected request must include a Bearer token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

**Token details:**
- Signed with an HMAC-SHA key (configured via `jwt.secret` in `application.properties`)
- Expires **1 hour** after issuance
- Carries the user's `role` claim embedded in the payload (e.g. `ROLE_ADMIN`)

The `/user/login` endpoint is the only public route — all other endpoints require a valid token.

---

## API Reference

Base URL: `http://localhost:8080`

All request/response bodies are **JSON**. Dates use the `YYYY-MM-DD` format.

---

### User Endpoints

#### Login

```
POST /user/login
```

Authenticates a user and returns a JWT token. **Public — no token required.**

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response `200 OK`** — returns the JWT token as a plain string:
```
eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9BRE1JTiIsInN1YiI6ImFkbWluIiwiaWF0...
```

**Response `401 Unauthorized`:**
```json
"Invalid username or Password"
```

---

#### Register a New User

```
POST /user/register
```

Creates a new user account. **Requires `ADMIN` role.**

**Request Body:**
```json
{
  "username": "analyst01",
  "password": "securepass",
  "role": "ANALYST"
}
```

Valid roles: `ADMIN`, `ANALYST`, `VIEWER`

**Response `200 OK`:**
```json
"User Added Successfully"
```

**Response `409 Conflict`** — username already taken:
```json
"Username already exists"
```

**Response `400 Bad Request`** — validation failure:
```json
{
  "password": "Password must be at least 8 characters",
  "username": "Username is required."
}
```

---

#### Delete a User

```
DELETE /user/delete/{username}
```

Permanently deletes a user by their username. **Requires `ADMIN` role.**

**Response `200 OK`:**
```json
"User with analyst01 deleted successfully."
```

**Response `404 Not Found`:**
```json
"User not found"
```

---

### Record Endpoints

#### Create a Financial Record

```
POST /records/create
```

Adds a new financial record. **Requires `ADMIN` role.**

**Request Body:**
```json
{
  "amount": 50000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2024-04-01",
  "notes": "April monthly salary"
}
```

`notes` is optional. `type` must be `INCOME` or `EXPENSE`.

**Response `200 OK`:**
```json
"Record created successfully."
```

**Response `400 Bad Request`** — validation failure:
```json
{
  "amount": "Amount must be positive",
  "category": "Category is required",
  "type": "Type is required",
  "date": "Date is required"
}
```

---

#### View / Filter Records

```
GET /records/view
```

Returns all records. Supports optional query parameters for filtering. **Requires `ADMIN` or `ANALYST` role.**

| Query Param | Type         | Description                                              |
|-------------|--------------|----------------------------------------------------------|
| `start`     | `YYYY-MM-DD` | Start of date range — must be used together with `end`   |
| `end`       | `YYYY-MM-DD` | End of date range — must be used together with `start`   |
| `category`  | `String`     | Case-insensitive partial match on category name          |
| `type`      | `RecordType` | Filter by `INCOME` or `EXPENSE`                          |

If no parameters are provided, all records are returned. Filter priority is: **date range → category → type** (only one filter applies per request).

**Example requests:**
```
GET /records/view
GET /records/view?start=2024-01-01&end=2024-03-31
GET /records/view?category=salary
GET /records/view?type=EXPENSE
```

**Response `200 OK`:**
```json
[
  {
    "recordId": 1,
    "amount": 50000.00,
    "type": "INCOME",
    "category": "Salary",
    "date": "2024-04-01",
    "notes": "April monthly salary"
  }
]
```

---

#### Update a Financial Record

```
PUT /records/update/{recordId}
```

Fully replaces an existing record. All required fields must be included. **Requires `ADMIN` role.**

**Request Body:**
```json
{
  "amount": 52000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2024-04-01",
  "notes": "Revised April salary"
}
```

**Response `200 OK`:**
```json
"Record updated Successfully."
```

**Response `404 Not Found`:**
```json
"Record not found"
```

---

#### Delete a Financial Record

```
DELETE /records/delete/{recordId}
```

Permanently deletes a record by its ID. **Requires `ADMIN` role.**

**Response `200 OK`:**
```json
"Deleted the record with 1 successfully."
```

**Response `404 Not Found`:**
```json
"Record not found"
```

---

#### Financial Dashboard

```
GET /records/dashboard
```

Returns a full financial summary. **Accessible to all authenticated roles (ADMIN, ANALYST, VIEWER).**

**Response `200 OK`:**
```json
{
  "Total income": 150000.0,
  "Total expenses": 72000.0,
  "Net balance": 78000.0,
  "Category wise totals": [
    {
      "Category": "Salary",
      "income": 150000.0,
      "expense": 0.0
    },
    {
      "Category": "Utilities",
      "income": 0.0,
      "expense": 12000.0
    }
  ],
  "Recent Activity": [
    {
      "recordId": 5,
      "amount": 3000.0,
      "type": "EXPENSE",
      "category": "Groceries",
      "date": "2024-04-10",
      "notes": null
    }
  ],
  "Monthly trend": [
    {
      "Month": 1,
      "income": 50000.0,
      "expense": 20000.0
    },
    {
      "Month": 4,
      "income": 50000.0,
      "expense": 15000.0
    }
  ]
}
```

The `Recent Activity` field contains the 5 most recently dated records. `Monthly trend` groups income and expenses by calendar month number.

### API Documentation URl


https://documenter.getpostman.com/view/37603669/2sBXiqFUmd

---

## Role & Permission Matrix

| Endpoint                         | ADMIN | ANALYST | VIEWER |
|----------------------------------|:-----:|:-------:|:------:|
| `POST /user/login`               | ✅    | ✅      | ✅     |
| `POST /user/register`            | ✅    | ❌      | ❌     |
| `DELETE /user/delete/{username}` | ✅    | ❌      | ❌     |
| `POST /records/create`           | ✅    | ❌      | ❌     |
| `GET /records/view`              | ✅    | ✅      | ❌     |
| `PUT /records/update/{id}`       | ✅    | ❌      | ❌     |
| `DELETE /records/delete/{id}`    | ✅    | ❌      | ❌     |
| `GET /records/dashboard`         | ✅    | ✅      | ✅     |

---

## Data Models

### Record

| Field      | Type         | Constraints                      |
|------------|--------------|----------------------------------|
| `recordId` | `Long`       | Auto-generated primary key       |
| `amount`   | `Double`     | Required, must be positive       |
| `type`     | `RecordType` | Required — `INCOME` or `EXPENSE` |
| `category` | `String`     | Required, non-blank              |
| `date`     | `LocalDate`  | Required — `YYYY-MM-DD`         |
| `notes`    | `String`     | Optional free-text               |

### User

| Field      | Type      | Constraints                                        |
|------------|-----------|----------------------------------------------------|
| `userId`   | `Long`    | Auto-generated primary key                         |
| `username` | `String`  | Required, unique                                   |
| `password` | `String`  | Required, min 8 chars — stored as BCrypt hash      |
| `role`     | `Role`    | Required — `ADMIN`, `ANALYST`, or `VIEWER`         |
| `active`   | `Boolean` | Defaults to `true` (not enforced in auth currently)|

---

## Assumptions Made

1. **Single active filter per request** — `GET /records/view` applies filters with a fixed priority (date range → category → type). Only one filter group is active per call; combining e.g. `category` and `type` in the same request will only apply the higher-priority filter.

2. **Full replacement on update** — `PUT /records/update/{id}` is a full replace, not a partial patch. All required fields must be re-submitted even when changing only one value.

3. **Records are not user-scoped** — Financial records belong to the organisation, not to individual users. Any user with sufficient role permissions can view or modify any record.

4. **Role is normalised to uppercase on save** — `UserService` calls `Role.valueOf(role.name().toUpperCase())` before persisting, so role values submitted in any casing are accepted and normalised.

5. **Token expiry is fixed at 1 hour** — There is no refresh token mechanism. Clients must re-authenticate via `/user/login` once a token expires.

6. **No soft delete** — Deletion of both users and records is permanent. There is no archival, deactivation, or recovery mechanism (the `active` flag on `User` exists in the model but is not currently enforced in authentication logic).

7. **CORS is open for development** — `SecurityConfig` allows all origin patterns (`*`). This should be tightened to specific allowed origins before any production deployment.

---

## Design Tradeoffs

### Stateless JWT vs Session-Based Auth
**Chosen:** Stateless JWT with `STATELESS` session policy.  
**Benefit:** Horizontally scalable — no shared session store required across instances.  
**Tradeoff:** Tokens cannot be invalidated before expiry (e.g. after logout or a role change). A token denylist backed by Redis would be needed for stricter security guarantees.

---

### Method-Level Security (`@PreAuthorize`) vs Centralised URL Rules
**Chosen:** `@PreAuthorize` annotations directly on controller methods, enabled via `@EnableMethodSecurity`.  
**Benefit:** Permission rules live alongside the code they guard, making each endpoint's access policy immediately visible during code review.  
**Tradeoff:** Permissions are scattered across controllers rather than centralised. As the API grows, a combined approach — coarse-grained URL rules in `SecurityFilterChain` plus fine-grained `@PreAuthorize` for specific cases — would be more maintainable.

---

### Flat Record Model with No User Ownership
**Chosen:** Records are global and not tied to the user who created them.  
**Benefit:** Simpler schema and query logic, well-suited for a shared organisational ledger.  
**Tradeoff:** Unsuitable for multi-tenant or personal-finance use cases. Adding a `createdBy` foreign key to `Record` and filtering on it in `RecordService` would support per-user data isolation.

---

### No Pagination on Record Listing
**Chosen:** `GET /records/view` returns the full result set as a list.  
**Benefit:** Simple client integration; no page/size bookkeeping needed for small datasets.  
**Tradeoff:** Will degrade in performance as the record count grows. `PageRequest` and `Sort` are already imported in `RecordController`, and Spring Data JPA's `Pageable` support is ready to be wired in — this is a clear, low-effort extension point.

---

### BCrypt Cost Factor of 12
**Chosen:** `new BCryptPasswordEncoder(12)` is used consistently in both `UserService` and `DataInitializer`.  
**Benefit:** Stronger resistance to brute-force attacks than the library default of 10.  
**Tradeoff:** Adds ~300–500ms of CPU time per password hash/verify operation. For high-throughput authentication scenarios, a lower factor or a memory-hard algorithm like Argon2 may be more appropriate.

---

### Inconsistent Error Response Shape
**Chosen:** Validation errors return a field-error map (`{ "field": "message" }`); not-found errors return a plain string.  
**Benefit:** Lightweight and readable during development.  
**Tradeoff:** Clients must handle two different error formats. A uniform error envelope — e.g. `{ "status": 404, "error": "Record not found", "timestamp": "..." }` — across all exception types would make client-side error handling significantly simpler and more predictable.
