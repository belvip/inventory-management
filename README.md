# Inventory Management — Spring Boot Backend

A production-ready REST API for inventory management built with Spring Boot 3 and Java 21. It powers the Inventory Pro frontend with full CRUD operations, role-based access control, JWT authentication, OAuth2 social login, image storage via MinIO, and email notifications.

🔗 **Live API:** [new-inventory-latest.onrender.com/api/v1](https://new-inventory-latest.onrender.com/api/v1)
📄 **Swagger UI:** [new-inventory-latest.onrender.com/swagger-ui.html](https://new-inventory-latest.onrender.com/swagger-ui.html)

---

## Table of Contents

1. [About](#about)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Architecture](#architecture)
5. [Project Structure](#project-structure)
6. [Getting Started](#getting-started)
7. [Configuration](#configuration)
8. [Security](#security)
9. [Contribution Guidelines](#contribution-guidelines)
10. [Roadmap](#roadmap)
11. [License](#license)
12. [Acknowledgements](#acknowledgements)
13. [Author](#author)

---

## About

This is the backend service for the Inventory Pro system — a multi-role inventory management platform designed for businesses to track stock, manage orders, handle suppliers and clients, and monitor sales activity.

The API is built following a layered architecture (Controller → Service → Repository) with clean separation of concerns, DTO mapping via MapStruct, and global exception handling. It is designed to be consumed by the [Inventory Pro frontend](https://github.com/belvinard-p/inventory-management-frontend) but can be integrated with any HTTP client.

---

## Features

- JWT authentication with access token and refresh token rotation
- OAuth2 social login via Google and GitHub
- Password reset flow via email (forgot password + token-based reset)
- Role-based access control — Admin, Manager, Sales, User
- Article management with image upload to MinIO object storage
- Category management with pagination and sorting
- Client management — full CRUD with address support
- Supplier management — full CRUD
- Client order management with order lines and status transitions
- Supplier order management with order lines and status transitions
- Sales management with line generation, finalization, and cancellation
- Stock validation — prevents orders when stock is insufficient
- Company management — multi-company support
- User management — Admin can update roles, lock/unlock, enable/disable accounts
- Paginated responses for all list endpoints
- Global exception handling with structured error responses
- OpenAPI / Swagger UI documentation
- Docker and Docker Compose support

---

## Tech Stack

**Core**
- Java 21
- Spring Boot 3.5.5
- Spring Web (REST API)
- Spring Data JPA (Hibernate)
- Spring Security
- Spring Validation (Bean Validation)

**Authentication & Authorization**
- JJWT 0.13.0 (JWT access + refresh tokens)
- Spring Security OAuth2 Client (Google, GitHub)
- BCrypt password encoding

**Database**
- PostgreSQL (production — Neon cloud or local)
- Hibernate DDL auto-update

**Object Storage**
- MinIO 8.5.17 (article image storage)

**Mapping**
- MapStruct 1.6.3 (DTO ↔ Entity mapping)
- Lombok 1.18.38 (boilerplate reduction)

**Email**
- Spring Boot Mail (SMTP — Gmail)

**Documentation**
- SpringDoc OpenAPI 2.5.0 (Swagger UI)

**Build & Deployment**
- Maven 3.9+ (Maven Wrapper included)
- Docker + Docker Compose
- Render (cloud deployment)

---

## Architecture

The application follows a standard layered Spring Boot architecture:

```
HTTP Client (Frontend / Swagger)
        │
        ▼
  Controller Layer          ← REST endpoints, request validation
        │
        ▼
  Service Layer             ← Business logic, stock validation, email
        │
        ▼
  Repository Layer          ← Spring Data JPA (PostgreSQL)
        │
        ▼
  PostgreSQL Database

External Services:
  ├── MinIO                 ← Article image storage
  ├── SMTP (Gmail)          ← Password reset emails
  ├── Google OAuth2         ← Social login
  └── GitHub OAuth2         ← Social login
```

**Security filter chain:**
```
Request → AuthTokenFilter (JWT validation) → SecurityFilterChain → Controller
```

All requests pass through the JWT filter. Public endpoints (auth, Swagger, OAuth2 callbacks) are explicitly permitted. All others require a valid Bearer token and the appropriate role.

---

## Project Structure

```
inventory-management/
├── src/main/java/com/belvinard/inventory_management/
│   ├── config/                   # App configuration
│   │   ├── AppConstant.java      # Pagination defaults
│   │   ├── MinioConfig.java      # MinIO client bean
│   │   ├── OAuth2LoginSuccessHandler.java
│   │   ├── OpenApiConfig.java    # Swagger/OpenAPI setup
│   │   └── WebConfig.java        # MVC converters (OrderStatus, ArticleStatus)
│   ├── controller/               # REST controllers (one per domain)
│   ├── dto/
│   │   ├── request/              # Incoming request DTOs
│   │   └── response/             # Outgoing response DTOs + PagedResponse
│   ├── exception/                # Custom exceptions + global handler
│   ├── mapper/                   # MapStruct mappers (Entity ↔ DTO)
│   ├── model/                    # JPA entities
│   ├── repository/               # Spring Data JPA repositories
│   ├── security/
│   │   ├── jwt/                  # JWT filter, utils, entry point
│   │   ├── request/              # Login, signup, token refresh requests
│   │   ├── response/             # Login, token refresh responses
│   │   ├── services/             # UserDetailsService implementation
│   │   └── SecurityConfig.java   # Filter chain, RBAC rules, seed data
│   ├── service/
│   │   ├── impl/                 # Service implementations
│   │   └── *.java                # Service interfaces
│   ├── utils/
│   │   └── EmailService.java     # Password reset email sender
│   ├── validation/               # Custom Bean Validation annotations
│   └── InventoryManagementApplication.java
├── src/main/resources/
│   └── application.properties    # All configuration (env-variable driven)
├── dummydatas/                   # Sample JSON payloads for testing
├── Dockerfile                    # Multi-stage Docker build (JDK 21)
├── docker-compose.yml            # App + MinIO services
├── env.template                  # Environment variable reference
├── pom.xml
└── mvnw / mvnw.cmd               # Maven wrapper
```

---

## Getting Started

**Prerequisites**
- Java 21
- Maven 3.9+ (or use the included `./mvnw`)
- PostgreSQL (local or cloud — Neon, Supabase, etc.)
- MinIO (local via Docker or cloud)

**1. Clone the repository**

```bash
git clone https://github.com/belvinard-p/inventory-management.git
cd inventory-management
```

**2. Set up environment variables**

Copy the template and fill in your values:

```bash
cp env.template .env
```

Edit `.env` with your database, MinIO, email, and OAuth credentials (see [Configuration](#configuration)).

**3. Run with Maven**

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8282/api/v1`.

**4. Run with Docker Compose** (includes MinIO)

```bash
docker-compose up --build
```

This starts both the Spring Boot app and a local MinIO instance.

**5. Access Swagger UI**

```
http://localhost:8282/swagger-ui.html
```

**Default test accounts** (seeded automatically on first run):

| Role    | Username  | Password   |
|---------|-----------|------------|
| Admin   | `admin`   | `password` |
| Manager | `manager` | `password` |
| Sales   | `sales`   | `password` |
| User    | `user`    | `password` |

**Build JAR**

```bash
./mvnw clean package -DskipTests
java -jar target/inventory-management-*.jar
```

---

## Configuration

All configuration is driven by environment variables loaded from a `.env` file at the project root.

| Variable                | Description                                      | Required |
|-------------------------|--------------------------------------------------|----------|
| `APP_PORT`              | Server port (default: `8282`)                    | Yes      |
| `DB_HOST`               | PostgreSQL host                                  | Yes      |
| `DB_PORT`               | PostgreSQL port (default: `5432`)                | Yes      |
| `DB_NAME`               | Database name                                    | Yes      |
| `DB_USER`               | Database username                                | Yes      |
| `DB_PASSWORD`           | Database password                                | Yes      |
| `JWT_SECRET_KEY`        | Base64-encoded secret for JWT signing (min 64 chars) | Yes  |
| `DEFAULT_USER_PASSWORD` | Default password for seeded accounts             | Yes      |
| `MINIO_URL`             | MinIO server URL (e.g. `http://localhost:9000`)  | Yes      |
| `MINIO_ACCESS_KEY`      | MinIO access key                                 | Yes      |
| `MINIO_SECRET_KEY`      | MinIO secret key                                 | Yes      |
| `MINIO_BUCKET_NAME`     | MinIO bucket name for article images             | Yes      |
| `FRONTEND_URL`          | Frontend origin for CORS and OAuth redirects     | Yes      |
| `MAIL_HOST`             | SMTP host (e.g. `smtp.gmail.com`)                | Yes      |
| `MAIL_PORT`             | SMTP port (e.g. `587`)                           | Yes      |
| `MAIL_USERNAME`         | SMTP username / sender email                     | Yes      |
| `MAIL_PASSWORD`         | SMTP app password                                | Yes      |
| `GITHUB_CLIENT_ID`      | GitHub OAuth2 app client ID                      | Optional |
| `GITHUB_CLIENT_SECRET`  | GitHub OAuth2 app client secret                  | Optional |
| `GOOGLE_CLIENT_ID`      | Google OAuth2 app client ID                      | Optional |
| `GOOGLE_CLIENT_SECRET`  | Google OAuth2 app client secret                  | Optional |

**JWT token lifetimes** (configured in `application.properties`):
- Access token: 15 minutes (`900000` ms)
- Refresh token: 24 hours (`86400000` ms)

---

## Security

- **JWT stateless authentication** — every request must include a valid `Authorization: Bearer <token>` header
- **BCrypt password hashing** — all passwords are hashed with BCrypt before storage
- **Refresh token rotation** — access tokens expire in 15 minutes; clients use the refresh token to obtain a new pair
- **Role-based access control** — endpoints are protected at the URL level in `SecurityConfig` with `hasRole` / `hasAnyRole` rules:
  - `ROLE_ADMIN` — full access including user management, company management, and sales finalization
  - `ROLE_MANAGER` — articles, categories, clients, orders, suppliers, companies
  - `ROLE_SALES` — read/create access to clients, orders, suppliers
  - `ROLE_USER` — read-only access to articles and categories
- **OAuth2 social login** — Google and GitHub tokens are validated server-side; a JWT is issued on success
- **Account lifecycle controls** — Admin can lock accounts, disable accounts, expire credentials, and expire accounts
- **CSRF disabled** — intentional for stateless JWT APIs consumed by SPAs
- **Session policy** — `IF_REQUIRED` with a maximum of 1 concurrent session per user
- **File upload limits** — max file size 1 MB, max request size 2 MB (configurable)
- **No secrets in source code** — all credentials are loaded from environment variables

---

## Contribution Guidelines

Contributions are welcome for bug fixes, new endpoints, or improvements.

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Follow the existing layered pattern: Controller → Service interface → ServiceImpl → Repository
4. Add a DTO for every new request/response — never expose entities directly
5. Use MapStruct for all entity ↔ DTO conversions
6. Add Bean Validation annotations on all request DTOs
7. Throw domain-specific exceptions (`ResourceNotFoundException`, `DuplicateResourceException`, etc.) — the global handler will format the response
8. Run `./mvnw verify` before submitting
9. Submit a pull request with a clear description of the change

**Code conventions:**
- Use Lombok (`@Data`, `@Builder`, `@RequiredArgsConstructor`) to reduce boilerplate
- Keep service implementations in `service/impl/` and interfaces in `service/`
- Paginate all list endpoints using `PagedResponse<T>`
- Use `@Value` for environment variable injection, never hardcode credentials

---

## Roadmap

**v1.0.0 — Completed**
- JWT + OAuth2 authentication
- Full CRUD for all domains (articles, categories, clients, suppliers, orders, sales)
- Role-based access control (Admin, Manager, Sales, User)
- MinIO image storage for articles
- Password reset via email
- Paginated responses
- Docker + Docker Compose support
- Swagger UI documentation

**v1.1.0 — Planned**
- Audit log for all write operations
- Export endpoints (PDF / Excel) for orders and sales
- Real-time stock alert notifications
- Two-factor authentication (2FA)
- Rate limiting on auth endpoints
- Integration tests with Testcontainers

---

## License

This project is for portfolio and demonstration purposes. All rights reserved © Belvinard Pouadjeu.

If you use this project as a reference or template, please credit the original author.

---

## Acknowledgements

- [Spring Boot](https://spring.io/projects/spring-boot) — Application framework
- [Spring Security](https://spring.io/projects/spring-security) — Authentication and authorization
- [JJWT](https://github.com/jwtk/jjwt) — JWT library for Java
- [MapStruct](https://mapstruct.org/) — Java bean mapping
- [Lombok](https://projectlombok.org/) — Boilerplate reduction
- [MinIO](https://min.io/) — S3-compatible object storage
- [SpringDoc OpenAPI](https://springdoc.org/) — Swagger UI integration
- [PostgreSQL](https://www.postgresql.org/) — Relational database
- [Neon](https://neon.tech/) — Serverless PostgreSQL hosting

---

## Author

**Belvinard Pouadjeu**
Fullstack Developer & Data Engineer

- Portfolio: [belvinard-resume.netlify.app](https://belvinard-resume.netlify.app/)
- GitHub: [github.com/belvinard-p](https://github.com/belvinard-p)
- LinkedIn: [linkedin.com/in/belvinard-pouadjeu-19a734377](https://www.linkedin.com/in/belvinard-pouadjeu-19a734377)
- Email: belvinar97@gmail.com
