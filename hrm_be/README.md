# 🏢 HRM Attendance Backend API

> A scalable, secure backend service for the Human Resource Management Attendance System - powering employee management, attendance tracking, and facial recognition integration.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-green.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.0-red.svg)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Backend Architecture](#-backend-architecture)
- [Technology Stack](#-technology-stack)
- [Key Features](#-key-features)
- [Authentication Flow](#-authentication-flow-jwt--refresh-token)
- [Redis Usage Explanation](#-redis-usage-explanation-token-storage-only)
- [API Structure](#-api-structure)
- [Integration with Face Recognition Service](#-integration-with-face-recognition-service)
- [Deployment Overview](#-deployment-overview)
- [How to Run Locally](#-how-to-run-locally-docker)
- [Environment Variables](#-environment-variables)
- [Future Improvements](#-future-improvements)
- [Author](#-author)

---

## 🎯 Project Overview

The **HRM Attendance Backend API** is the core backend component of a distributed Human Resource Management Attendance System. This microservice handles employee data management, attendance record processing, secure authentication, and seamless integration with a Python-based Face Recognition microservice for biometric attendance tracking.

Built with enterprise-grade security and scalability in mind, this service supports multi-role authorization, stateless JWT authentication with refresh token rotation, and robust data persistence for mission-critical HR operations.

### 🏗️ System Context

```
┌─────────────────┐     ┌──────────────────────┐     ┌─────────────────┐
│   Web Client    │────▶│   HRM Backend API    │────▶│   MySQL         │
│   (React/Vue)   │◀────│   (This Service)     │     │   (Primary DB)  │
└─────────────────┘     └──────────────────────┘     └─────────────────┘
                                 │
                                 │ HTTP/REST
                                 ▼
                        ┌──────────────────────┐
                        │  Face Recognition    │
                        │  Microservice        │
                        │  (Python/FastAPI)    │
                        └──────────────────────┘
```

---

## 🏛️ Backend Architecture

The service follows a **modular monolith** architecture with clear domain boundaries, ensuring maintainability and future scalability.

### Architectural Principles

| Principle | Implementation |
|-----------|----------------|
| **Stateless Design** | JWT-based authentication, no server-side sessions |
| **Layered Architecture** | Controller → Service → Repository pattern |
| **Domain-Driven Modules** | Organized by business capability (employee, attendance, payroll, etc.) |
| **Dependency Injection** | Spring IoC container for loose coupling |
| **DTO Pattern** | MapStruct for entity-to-DTO mapping |
| **Security First** | Spring Security with role-based access control |

### Module Structure

```
modules/
├── attendance/          # Attendance records, check-in/out, reports
├── auth/               # Authentication & authorization logic
├── contract/           # Employee contracts & agreements
├── employee/           # Employee master data management
├── face_recognition/   # Face registration & recognition coordination
├── file/               # Document upload & management
├── leave/              # Leave requests, approvals & balances
├── organization/       # Departments, positions, org structure
├── payroll/            # Salary calculation & payslip generation
├── penalty/            # Attendance penalties & deductions
└── user/               # User account management
```

---

## 🛠️ Technology Stack

### Core Framework
| Technology | Version | Purpose |
|------------|---------|---------|
| **Spring Boot** | 3.4.1 | Application framework |
| **Spring Security** | 6.x | Authentication & authorization |
| **Spring Data JPA** | 3.4.x | Data persistence layer |
| **Spring Data Redis** | 3.4.x | Token storage |
| **Java** | 21 | Programming language |

### Data Layer
| Technology | Purpose |
|------------|---------|
| **MySQL 8.0** | Primary relational database |
| **Redis** | Refresh token storage with TTL |
| **HikariCP** | High-performance connection pooling |

### Security & Auth
| Technology | Purpose |
|------------|---------|
| **Nimbus JOSE JWT** | JWT token generation & validation |
| **BCrypt** | Password encryption |

### Utilities
| Technology | Purpose |
|------------|---------|
| **MapStruct** | Object mapping (Entity ↔ DTO) |
| **Lombok** | Boilerplate code reduction |
| **Apache POI** | Excel import/export for bulk operations |
| **Thumbnailator** | Image processing for face photos |
| **Jakarta Mail** | Email notifications |

---

## ✨ Key Features

### 👥 Employee Management
- Complete employee lifecycle management (onboarding to offboarding)
- Document attachment support
- Bulk import/export via Excel
- Employee photo management for facial recognition

### ⏰ Attendance Tracking
- Check-in/out record management
- Integration with Face Recognition microservice for biometric verification
- Attendance status evaluation (Present, Absent, Late, Early Leave)
- Overtime calculation

### 🔐 Authentication & Authorization
- Stateless JWT authentication
- Dual token mechanism (Access Token + Refresh Token)
- Role-based access control (RBAC) with Spring Security
- Secure logout with token revocation
- Account activation via email

### 📊 Payroll & Compensation
- Automated payroll calculation
- Penalty deduction processing
- Payslip generation with Excel export
- Payroll approval workflow

### 🏖️ Leave Management
- Leave request submission & approval workflow
- Leave balance tracking
- Multiple leave types support
- Automatic leave accrual

### 🏢 Organization Structure
- Department management
- Position hierarchy
- Organizational reporting lines

---

## 🔐 Authentication Flow (JWT + Refresh Token)

The system implements a secure dual-token authentication mechanism to balance security and user experience.

### Token Specifications

| Token Type | Storage | Lifetime | Purpose |
|------------|---------|----------|---------|
| **Access Token** | Client Memory | 15 minutes | API authorization (short-lived) |
| **Refresh Token** | HTTP-only Cookie | 30 days | Token renewal (long-lived, stored in Redis) |

### Authentication Sequence

```
┌─────────┐                                    ┌─────────────┐     ┌────────┐
│ Client  │                                    │ HRM Backend │     │ Redis  │
└────┬────┘                                    └──────┬──────┘     └───┬────┘
     │                                                │                │
     │ POST /api/v1/auth/login                        │                │
     │ { username, password }                         │                │
     │───────────────────────────────────────────────▶│                │
     │                                                │                │
     │                                                │ Verify credentials
     │                                                │ Generate tokens
     │                                                │                │
     │                                                │ Store refresh  │
     │                                                │ token with TTL │
     │                                                │───────────────▶│
     │                                                │                │
     │ 200 OK { accessToken }                         │                │
     │ Set-Cookie: refreshToken=...                   │                │
     │◀───────────────────────────────────────────────│                │
     │                                                │                │
     │─────────────────────────────────────────────────────────────────▶│
     │ API calls with Authorization: Bearer {accessToken}               │
     │                                                │                │
     │◀─────────────────────────────────────────────────────────────────│
     │ 200 OK (protected resource)                    │                │
     │                                                │                │
     │ [After 15 min - Access Token Expired]          │                │
     │                                                │                │
     │ POST /api/v1/auth/refresh                      │                │
     │ Cookie: refreshToken=...                       │                │
     │───────────────────────────────────────────────▶│                │
     │                                                │ Validate       │
     │                                                │ from Redis     │
     │                                                │◀───────────────│
     │                                                │                │
     │ 200 OK { accessToken }                         │                │
     │◀───────────────────────────────────────────────│                │
```

### Logout & Token Revocation

- **Single Device Logout**: Revokes the specific refresh token
- **Global Logout**: Revokes all refresh tokens for the user across all devices

---

## 🔴 Redis Usage Explanation (Token Storage ONLY)

> ⚠️ **Important Clarification**: Redis is used **exclusively** for refresh token storage and token validation. It is **NOT** used for data caching.

### Purpose

Redis serves as a high-availability token storage layer for:

1. **Refresh Token Persistence**: Storing active refresh tokens with automatic expiration (TTL)
2. **Token Revocation**: Enabling secure logout by deleting tokens from storage
3. **Multi-device Session Management**: Tracking all active sessions per user

### Redis Data Structure

```java
@RedisHash("refreshToken")
public class RefreshToken {
    @Id
    private String jwtID;       // Unique token identifier
    @Indexed
    private String username;    // For querying all user tokens
    @TimeToLive
    private Long ttl;           // Auto-expiration (30 days)
}
```

### Why Redis for Token Storage?

| Advantage | Benefit |
|-----------|---------|
| **TTL Support** | Automatic token expiration without cleanup jobs |
| **Fast Lookups** | O(1) token validation for high-throughput APIs |
| **Indexed Queries** | Efficient retrieval of all tokens by username |
| **Persistence** | Survives application restarts (configurable) |
| **Cluster Support** | Scalable for distributed deployments |

### What is NOT Stored in Redis

❌ Employee data  
❌ Attendance records  
❌ Payroll calculations  
❌ Session state  
❌ API response data  

> All business data is persisted in **MySQL** as the single source of truth.

---

## 🌐 API Structure

### Base URL
```
/api/v1
```

### API Categories

| Module | Base Path | Description |
|--------|-----------|-------------|
| Authentication | `/api/v1/auth/**` | Login, logout, token refresh, account activation |
| Employee | `/api/v1/employees/**` | Employee CRUD operations |
| Attendance | `/api/v1/attendance/**` | Attendance records & reports |
| Attendance Scan | `/api/v1/attendance-scan/**` | Face recognition check-in/out |
| Leave | `/api/v1/leaves/**` | Leave requests & approvals |
| Payroll | `/api/v1/payrolls/**` | Payroll processing |
| Contract | `/api/v1/contracts/**` | Employee contracts |
| Organization | `/api/v1/departments/**` | Org structure |
| File | `/api/v1/files/**` | Document uploads |

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1/auth/login` | User login | ❌ |
| POST | `/api/v1/auth/logout` | Single device logout | ✅ |
| POST | `/api/v1/auth/logout-all` | Global logout | ✅ |
| POST | `/api/v1/auth/refresh` | Refresh access token | ❌ (cookie) |
| POST | `/api/v1/auth/active-account` | Activate account | ❌ |

### Response Format

```json
{
  "code": 1000,
  "message": "Success",
  "data": { ... }
}
```

### Error Format

```json
{
  "code": 1001,
  "message": "Invalid credentials"
}
```

---

## 🎭 Integration with Face Recognition Service

The backend orchestrates communication with a dedicated Python-based Face Recognition microservice for biometric attendance processing.

### Architecture

```
┌─────────────────┐     HTTP/REST      ┌─────────────────────────┐
│   HRM Backend   │◄──────────────────►│  Face Recognition       │
│   (This API)    │                    │  Microservice (Python)  │
└────────┬────────┘                    └─────────────────────────┘
         │
         │ 1. Register employee face
         │ POST /facial-recognition/register-face
         │ { employeeId, faceImage }
         │
         │ 2. Register multiple faces (batch)
         │ POST /facial-recognition/register-face-batch
         │
         │ 3. Recognize face for attendance
         │ POST /facial-recognition/face-recognition
         │ { faceImage } → { employeeId, confidence }
         │
         │ 4. Update face data
         │ POST /facial-recognition/update-face
         │
         │ 5. Delete face data
         │ POST /facial-recognition/delete-face
```

### Attendance Flow with Face Recognition

1. **Employee stands before camera**
2. **Frontend captures face image**
3. **Backend forwards image to Face Recognition service**
4. **Service returns identified employee ID + confidence score**
5. **Backend validates and creates attendance record**
6. **Response returned to frontend with check-in/out status**

### Configuration

```yaml
face-recognition:
  base-url: ${FACE_RECOGNITION_BASE_URL:http://127.0.0.1:8000}
  endpoints:
    register: /facial-recognition/register-face
    recognize: /facial-recognition/face-recognition
    update: /facial-recognition/update-face
    delete: /facial-recognition/delete-face
    register-batch: /facial-recognition/register-face-batch
```

---

## 🚀 Deployment Overview

### Production Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                           AWS EC2                                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                      Nginx (Reverse Proxy)               │   │
│  │                   SSL/TLS Termination                    │   │
│  └─────────────────────────┬───────────────────────────────┘   │
│                            │                                     │
│                            ▼                                     │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              Docker Compose Stack                        │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │   │
│  │  │  HRM Backend │  │    Redis     │  │  (MySQL)     │  │   │
│  │  │   (Docker)   │  │   (Docker)   │  │  (TiDB Cloud)│  │   │
│  │  │   Port 8081  │  │   Port 6379  │  │              │  │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘  │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### Infrastructure Components

| Component | Technology | Purpose |
|-----------|------------|---------|
| **Cloud Provider** | AWS EC2 (Linux) | Compute infrastructure |
| **Reverse Proxy** | Nginx | Load balancing, SSL termination, static file serving |
| **SSL/TLS** | Let's Encrypt / AWS ACM | HTTPS encryption |
| **Containerization** | Docker + Docker Compose | Service orchestration |
| **Database** | TiDB Cloud (MySQL-compatible) | Managed distributed database |
| **Token Storage** | Redis Cloud | Managed Redis instance |

### Security Measures

- ✅ HTTPS-only API access
- ✅ HTTP-only cookies for refresh tokens
- ✅ Secure cookie flags (Secure, SameSite)
- ✅ Non-root Docker containers
- ✅ Environment variable secrets management
- ✅ CORS configuration for specific origins

---

## 🐳 How to Run Locally (Docker)

### Prerequisites

- [Docker](https://www.docker.com/get-started) 20.10+
- [Docker Compose](https://docs.docker.com/compose/install/) 2.0+

### Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/TRONGG2005k/hrm
   cd hrm-backend
   ```

2. **Create environment file**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Start services with Docker Compose**
   ```bash
   docker-compose up -d
   ```

4. **Verify services are running**
   ```bash
   # Health check
   curl http://localhost:8081/api/v1/hello
   
   # Check logs
   docker-compose logs -f app
   ```

### Local Development Setup

For development without Docker:

1. **Prerequisites**
   - Java 21 JDK
   - MySQL 8.0
   - Redis 7.0
   - Maven 3.9+

2. **Database setup**
   ```sql
   CREATE DATABASE hrm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Configure application**
   Set environment variables or update `application.yml` with your local settings.

4. **Run the application**
   ```bash
   cd hrm
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

5. **Access API documentation**
   - Base URL: `http://localhost:8080`
   - Health Check: `http://localhost:8080/api/v1/hello`

---

## ⚙️ Environment Variables

### Database Configuration

| Variable | Description | Default (Dev) |
|----------|-------------|---------------|
| `DB_URL` | MySQL JDBC URL | `jdbc:mysql://localhost:3306/hrm_db` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | `123456` |
| `DB_DRIVER` | JDBC driver class | `com.mysql.cj.jdbc.Driver` |
| `DB_POOL_MAX_SIZE` | Connection pool max size | `10` |
| `DB_POOL_MIN_IDLE` | Connection pool min idle | `5` |

### Redis Configuration

| Variable | Description | Default (Dev) |
|----------|-------------|---------------|
| `REDIS_HOST` | Redis server host | `localhost` |
| `REDIS_PORT` | Redis server port | `6379` |
| `REDIS_PASSWORD` | Redis password | *(empty)* |

### JWT Configuration

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_SIGNER_KEY_ACCESS` | Secret key for access tokens | *(required)* |
| `JWT_SIGNER_KEY_REFRESH` | Secret key for refresh tokens | *(required)* |
| `JWT_SIGNER_KEY_ACTIVATION` | Secret key for activation tokens | *(required)* |
| `JWT_ACCESS_DURATION` | Access token lifetime (seconds) | `900` (15 min) |
| `JWT_REFRESH_DURATION` | Refresh token lifetime (seconds) | `2592000` (30 days) |

### Application Settings

| Variable | Description | Default (Dev) |
|----------|-------------|---------------|
| `SERVER_PORT` | Application port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `pro` |
| `API_PREFIX` | API base path prefix | `/api/v1` |
| `UPLOAD_DIR` | File upload directory | `./uploads` |
| `UPLOAD_MAX_FILE_SIZE` | Max upload file size | `50MB` |

### Email Configuration

| Variable | Description | Default |
|----------|-------------|---------|
| `MAIL_HOST` | SMTP server host | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP server port | `587` |
| `MAIL_USERNAME` | SMTP username | *(required)* |
| `MAIL_PASSWORD` | SMTP password | *(required)* |
| `MAIL_BASE_URL` | Account activation base URL | `http://localhost:5173/active-account` |

### Face Recognition Service

| Variable | Description | Default |
|----------|-------------|---------|
| `FACE_RECOGNITION_BASE_URL` | Face recognition API base URL | `http://127.0.0.1:8000` |

---

## 🔮 Future Improvements

### Short-term
- [ ] OpenAPI/Swagger documentation integration
- [ ] Rate limiting for API endpoints
- [ ] Request/Response logging middleware
- [ ] Enhanced audit logging for compliance

### Mid-term
- [ ] API versioning strategy (v1, v2)
- [ ] Event-driven architecture with message queue
- [ ] Microservices decomposition
- [ ] Distributed tracing with OpenTelemetry
- [ ] Prometheus metrics & Grafana dashboards

### Long-term
- [ ] Multi-tenancy support for SaaS model
- [ ] Advanced analytics & reporting engine
- [ ] Mobile push notification service
- [ ] Integration with external HR systems (SAP, Workday)
- [ ] AI-powered attendance anomaly detection

---

## 👤 Author

**Your Name**


- 💻 GitHub: [TRONGG2005k](https://github.com/TRONGG2005k/hrm)
- 📧 Email: tn0961350951@gmail.com

---

## 📄 License

This project is proprietary software. All rights reserved.

---

<p align="center">
  <sub>Built with ❤️ for efficient workforce management</sub>
</p>
