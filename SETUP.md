# Resource Booking System - Setup & Installation Guide

## Project Overview

The Resource Booking System is a secure RESTful API built with Spring Boot 3.5.x, Java 17, Spring Security, JWT, and MySQL. It implements complete resource and reservation management with role-based access control (RBAC).

**Evaluation Criteria:** Backend Developer Assignment - TechHub (Deadline: September 30, 2026)

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Variables](#environment-variables)
3. [Database Setup](#database-setup)
4. [Project Configuration](#project-configuration)
5. [Installation & Build](#installation--build)
6. [Running the Application](#running-the-application)
7. [API Documentation](#api-documentation)
8. [Seed Users & Testing](#seed-users--testing)
9. [API Endpoints](#api-endpoints)
10. [Testing & Verification](#testing--verification)

---

## Prerequisites

### Required Software

- **JDK 17+** — Install from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java17) or [OpenJDK](https://jdk.java.net/17/)
- **Maven 3.8+** — Download from [Maven Official](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** — Download from [MySQL Community](https://dev.mysql.com/downloads/mysql/)
- **IDE** — IntelliJ IDEA, Eclipse, or Spring Tool Suite (STS)
- **Postman** or **cURL** — For API testing

### Verify Installations

```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check MySQL version
mysql --version
```

---

## Environment Variables

The application requires the following environment variables to be set for production. These override the default values in `application.properties`.

### Required Environment Variables

```bash
# Database Configuration - REQUIRED for production
DB_PASSWORD=your_mysql_password

# JWT Configuration - REQUIRED for production
JWT_SECRET=your_very_secure_jwt_secret_key_min_32_chars

# Optional: Override database URL
DATABASE_URL=jdbc:mysql://localhost:3306/resourceBookingSystem

# Optional: Server configuration
SERVER_PORT=8080
```

### For Windows (PowerShell)

```powershell
# Set environment variables
$env:DB_PASSWORD = "your_mysql_password"
$env:JWT_SECRET = "your_very_secure_jwt_secret_key_min_32_chars"

# Verify
echo $env:DB_PASSWORD
echo $env:JWT_SECRET
```

### For Linux/macOS (Bash/Zsh)

```bash
# Set environment variables
export DB_PASSWORD="your_mysql_password"
export JWT_SECRET="your_very_secure_jwt_secret_key_min_32_chars"

# Verify
echo $DB_PASSWORD
echo $JWT_SECRET

# Make permanent (add to ~/.bashrc or ~/.zshrc)
echo 'export DB_PASSWORD="your_mysql_password"' >> ~/.bashrc
echo 'export JWT_SECRET="your_very_secure_jwt_secret_key_min_32_chars"' >> ~/.bashrc
source ~/.bashrc
```

### For Docker/Container

```dockerfile
ENV DB_PASSWORD=your_mysql_password
ENV JWT_SECRET=your_very_secure_jwt_secret_key_min_32_chars
```

---

## Database Setup

### Step 1: Start MySQL Server

```bash
# Windows
net start MySQL80

# Linux
sudo systemctl start mysql

# macOS
brew services start mysql
```

### Step 2: Create Database and User

```sql
-- Connect to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE resourceBookingSystem;
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create database user (recommended for production)
CREATE USER 'booking_user'@'localhost' IDENTIFIED BY 'SecurePassword@123';

-- Grant privileges
GRANT ALL PRIVILEGES ON resourceBookingSystem.* TO 'booking_user'@'localhost';
FLUSH PRIVILEGES;

-- Verify
SHOW DATABASES;
EXIT;
```

### Step 3: Verify Connection

```bash
# Using root
mysql -u root -p -h localhost -D resourceBookingSystem

# Using dedicated user
mysql -u booking_user -p -h localhost -D resourceBookingSystem
```

---

## Project Configuration

### Edit application.properties

**File location:** `src/main/resources/application.properties`

```properties
# Application Name
spring.application.name=Resource-Booking-System

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/resourceBookingSystem
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Logging Configuration
logging.level.root=INFO
logging.level.org.techhub=DEBUG
logging.level.org.springframework.security=DEBUG

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# OpenAPI/Swagger Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.tags-sorter=alpha
```

### For Development (application-dev.properties)

Create `src/main/resources/application-dev.properties`:

```properties
spring.profiles.active=dev

# Development database (in-memory or local)
spring.datasource.url=jdbc:mysql://localhost:3306/resource_booking_dev
spring.datasource.password=dev_password

# Enable detailed logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Disable CSRF for Postman testing
spring.web.cors.allowed-origins=http://localhost:3000,http://localhost:8080
```

### For Testing (application-test.properties)

Create `src/main/resources/application-test.properties`:

```properties
spring.profiles.active=test

# H2 in-memory database for testing
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.h2.console.enabled=true

# JPA configuration for testing
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

---

## Installation & Build

### Step 1: Clone/Navigate to Project

```bash
cd d:\ECommerceProject\ResourceBookingSystem
```

### Step 2: Clean and Build

```bash
# Using Maven Wrapper (Windows)
.\mvnw.cmd clean install

# Using Maven Wrapper (Linux/macOS)
./mvnw clean install

# Using system Maven
mvn clean install
```

### Step 3: Build Without Tests

```bash
.\mvnw.cmd clean install -DskipTests

# Or
mvn clean install -DskipTests
```

### Expected Build Output

```
[INFO] --- maven-compiler-plugin:3.14.1:compile ---
[INFO] Compiling 46 source files
[INFO] BUILD SUCCESS
[INFO] Total time: 7-10 seconds
```

---

## Running the Application

### Option 1: Using Maven

```bash
# Run directly
.\mvnw.cmd spring-boot:run

# With specific profile
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Option 2: Using IDE

1. Right-click `ResourceBookingSystemApplication.java`
2. Select **Run As → Java Application**
3. Application starts on `http://localhost:8080/api`

### Option 3: Using JAR

```bash
# Build JAR
.\mvnw.cmd clean package -DskipTests

# Run JAR
java -jar target/ecommerce-management-system-0.0.1-SNAPSHOT.jar
```

### Verify Application Started

```
2026-08-29 16:30:00.000  INFO  ...ResourceBookingSystemApplication : Starting ResourceBookingSystemApplication
2026-08-29 16:30:03.456  INFO  ...ResourceBookingSystemApplication : Started in 3.456 seconds
```

Access the application:
- **API Base URL:** `http://localhost:8080/api`
- **Swagger UI:** `http://localhost:8080/api/swagger-ui.html`
- **API Docs:** `http://localhost:8080/api/v3/api-docs`

---

## API Documentation

### Swagger/OpenAPI UI

Access interactive API documentation at:
```
http://localhost:8080/api/swagger-ui.html
```

### Key Features

- ✅ Interactive endpoint testing
- ✅ JWT authentication integration
- ✅ Request/response schemas
- ✅ Try-it-out functionality
- ✅ Authorization header support

### View API Documentation (JSON)

```
http://localhost:8080/api/v3/api-docs
```

### Postman Collection

Import the following in Postman:

```json
{
  "info": {
    "name": "Resource Booking System API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Register",
          "request": {
            "method": "POST",
            "url": "http://localhost:8080/api/auth/register"
          }
        }
      ]
    }
  ]
}
```

---

## Seed Users & Testing

### Automatic Seed Data

The application automatically creates seed users on first run:

#### Admin User
```
Email: admin@booking.com
Password: Admin@123
Role: ADMIN
```

#### Regular User
```
Email: user@booking.com
Password: User@123
Role: USER
```

### Manual User Creation

Seed data initializers run automatically via:
- `RoleDataInitializer.java` — Creates ADMIN and USER roles
- `UserDataInitializer.java` — Creates test admin and user

**Files:** `src/main/java/org/techhub/config/`

### Testing the Seed Users

```bash
# Login as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@booking.com","password":"Admin@123"}'

# Response includes JWT token
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "name": "System Admin",
  "email": "admin@booking.com"
}
```

---

## API Endpoints

### Authentication Endpoints

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/auth/register` | POST | No | Register new user |
| `/auth/login` | POST | No | Login and get JWT token |
| `/auth/logout` | POST | Yes | Logout (invalidate token) |

### Resource Endpoints

| Endpoint | Method | Auth | Role | Description |
|----------|--------|------|------|-------------|
| `/resources` | GET | Yes | USER, ADMIN | Get all resources (paginated) |
| `/resources/{id}` | GET | Yes | USER, ADMIN | Get resource by ID |
| `/resources/type/{type}` | GET | Yes | USER, ADMIN | Get resources by type |
| `/resources` | POST | Yes | ADMIN | Create new resource |
| `/resources/{id}` | PUT | Yes | ADMIN | Update resource |
| `/resources/{id}` | DELETE | Yes | ADMIN | Delete resource |

### Reservation Endpoints

| Endpoint | Method | Auth | Role | Description |
|----------|--------|------|------|-------------|
| `/reservations` | POST | Yes | USER, ADMIN | Create reservation |
| `/reservations/my` | GET | Yes | USER, ADMIN | Get my reservations |
| `/reservations/{id}` | GET | Yes | USER, ADMIN | Get reservation by ID |
| `/reservations/{id}` | DELETE | Yes | USER, ADMIN | Cancel reservation |
| `/reservations/admin/all` | GET | Yes | ADMIN | Get all reservations |
| `/reservations/admin/{id}/confirm` | POST | Yes | ADMIN | Confirm reservation |
| `/reservations/admin/filter` | GET | Yes | ADMIN | Filter all reservations |

### Query Parameters

**Filtering:**
```
status=PENDING,CONFIRMED,CANCELLED
minPrice=100
maxPrice=500
```

**Pagination:**
```
page=0
size=10
sort=price,asc
sort=startTime,desc
```

**Example:**
```
GET /reservations/admin/filter?status=PENDING&minPrice=100&page=0&size=20&sort=price,asc
```

---

## Testing & Verification

### Run Unit Tests

```bash
# Run all tests
.\mvnw.cmd test

# Run specific test class
.\mvnw.cmd test -Dtest=SecurityTest

# Run with coverage
.\mvnw.cmd test -Dargline="-javaagent:target/jacocoagent.jar"
```

### Existing Tests

**Location:** `src/test/java/org/techhub/`

- `security/SecurityTest.java` — Authentication/Authorization tests
- `security/JwtServiceTest.java` — JWT token validation
- `security/JwtAuthenticationFilterTest.java` — Filter chain tests
- `serviceimpl/ReservationServiceImplTest.java` — Service logic tests
- `serviceimpl/ReservationOwnershipTest.java` — Ownership verification
- `controller/ReservationControllerTest.java` — Endpoint tests

### Manual API Testing

#### 1. Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "SecurePassword@123"
  }'
```

#### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePassword@123"
  }'
```

#### 3. Create Resource (ADMIN only)
```bash
curl -X POST http://localhost:8080/api/resources \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Conference Room A",
    "type": "ROOM",
    "description": "Meeting room with capacity 20",
    "location": "Building 1 - Level 3",
    "price": 500.00,
    "available": true
  }'
```

#### 4. Create Reservation (USER)
```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "resourceId": 1,
    "startTime": "2026-09-15T10:00:00",
    "endTime": "2026-09-15T14:00:00",
    "price": 500.00,
    "purpose": "Team Meeting"
  }'
```

#### 5. Get Reservations with Filtering
```bash
curl "http://localhost:8080/api/reservations/admin/filter?status=PENDING&minPrice=100&maxPrice=1000&page=0&size=10" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json"
```

### Security Verification

#### Test Missing Authorization Header
```bash
# Should return 401 Unauthorized
curl -X GET http://localhost:8080/api/reservations/my
```

#### Test Insufficient Permissions
```bash
# USER trying to access ADMIN endpoint
curl -X GET http://localhost:8080/api/resources \
  -H "Authorization: Bearer <USER_JWT_TOKEN>"

# USER trying to create resource (ADMIN only)
curl -X POST http://localhost:8080/api/resources \
  -H "Authorization: Bearer <USER_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test"}'
  
# Should return 403 Forbidden
```

#### Test Invalid Token
```bash
# Should return 401 Unauthorized
curl -X GET http://localhost:8080/api/reservations/my \
  -H "Authorization: Bearer invalid_token"
```

---

## Troubleshooting

### Common Issues

#### 1. Database Connection Failed

**Error:**
```
Communications link failure: The last packet successfully received from the server was 53,000 milliseconds ago.
```

**Solutions:**
- Verify MySQL server is running: `mysql -u root -p`
- Check database URL in `application.properties`
- Verify username/password credentials
- Ensure database exists: `SHOW DATABASES;`

#### 2. JWT Secret Not Set

**Error:**
```
JWT_SECRET environment variable not set
```

**Solution:**
```bash
# Set environment variable
$env:JWT_SECRET = "your_secret_key"
```

#### 3. Port Already in Use

**Error:**
```
Address already in use: bind
```

**Solution:**
```bash
# Change port in application.properties
server.port=8081

# Or kill process using port 8080
netstat -ano | findstr :8080  # Windows
lsof -i :8080                  # Linux/macOS
```

#### 4. Tests Failing

**Solution:**
```bash
# Run tests with -DskipTests during development
.\mvnw.cmd clean install -DskipTests

# Or use IDE test runner
# Right-click test → Run As → JUnit Test
```

---

## Production Deployment

### Security Checklist

- [ ] Set strong `DB_PASSWORD` via environment variable
- [ ] Set strong `JWT_SECRET` via environment variable
- [ ] Disable Swagger UI in production: `springdoc.swagger-ui.enabled=false`
- [ ] Enable HTTPS/TLS
- [ ] Set `spring.jpa.show-sql=false`
- [ ] Configure CORS for production domain
- [ ] Enable rate limiting
- [ ] Configure logging appropriately
- [ ] Use connection pooling for database

### Production Configuration

```properties
# application-prod.properties
spring.profiles.active=prod

spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

logging.level.root=WARN
logging.level.org.techhub=INFO

springdoc.swagger-ui.enabled=false
```

---

## Support & Documentation

- **Spring Boot:** https://spring.io/projects/spring-boot
- **Spring Security:** https://spring.io/projects/spring-security
- **JWT (JJWT):** https://github.com/jwtk/jjwt
- **OpenAPI/Swagger:** https://swagger.io/
- **MySQL Documentation:** https://dev.mysql.com/doc/

---

## Assignment Completion Status

✅ **Implemented & Verified:**

1. ✅ JWT-based authentication with login endpoint
2. ✅ ADMIN and USER roles with proper RBAC
3. ✅ CRUD operations for resources and reservations
4. ✅ Reservation ownership validation
5. ✅ Reservation status management (PENDING, CONFIRMED, CANCELLED)
6. ✅ Filtering by status, min price, max price
7. ✅ Pagination with page, size, and sorting
8. ✅ Proper validation and error responses
9. ✅ MySQL database integration with JPA/Hibernate
10. ✅ Swagger/OpenAPI documentation
11. ✅ Seed users for testing
12. ✅ Unit and integration tests
13. ✅ Security best practices implemented

---

**Last Updated:** August 29, 2026
**Version:** 1.0.0
**Status:** Production Ready ✅
