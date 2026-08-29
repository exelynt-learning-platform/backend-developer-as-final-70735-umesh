# Resource Booking System

A secure RESTful Resource Booking System developed using Java 17, Spring Boot 3.5.x, Spring Security, JWT, JPA/Hibernate, and MySQL.

The system provides user authentication, role-based authorization, resource management, and reservation management.

## Features

Authentication:

* User Registration
* User Login
* JWT Authentication
* BCrypt Password Encryption
* USER and ADMIN Roles
* Role-Based Access Control

Resource Management:

* View Resources
* View Resource by ID
* Create Resource - ADMIN
* Update Resource - ADMIN
* Delete Resource - ADMIN
* Resource Availability Management

Reservation Management:

* Create Reservation
* View My Reservations
* View Reservation by ID
* Cancel Reservation
* View All Reservations - ADMIN
* Confirm Reservation - ADMIN
* Start and End Time Validation
* Overlapping Reservation Prevention

Filtering and Pagination:

* Status Filtering
* Minimum Price Filtering
* Maximum Price Filtering
* Sorting
* Pagination

## Technology Stack

Java 17
Spring Boot 3.5.x
Spring Security
JWT
Spring Data JPA
Hibernate
MySQL
Maven
Swagger / OpenAPI
Postman

## Project Structure

ResourceBookingSystem
|
|-- src
|   |
|   |-- main
|   |   |
|   |   |-- java
|   |   |   |
|   |   |   |-- org
|   |   |       |
|   |   |       |-- techhub
|   |   |           |
|   |   |           |-- ResourceBookingSystemApplication.java
|   |   |           |
|   |   |           |-- config
|   |   |           |   |-- SecurityConfig.java
|   |   |           |   |-- SecurityBeansConfig.java
|   |   |           |   |-- PasswordConfig.java
|   |   |           |
|   |   |           |-- controller
|   |   |           |   |-- AuthController.java
|   |   |           |   |-- ResourceController.java
|   |   |           |   |-- ReservationController.java
|   |   |           |
|   |   |           |-- dto
|   |   |           |   |-- RegisterRequest.java
|   |   |           |   |-- LoginRequest.java
|   |   |           |   |-- LoginResponse.java
|   |   |           |   |-- ResourceRequest.java
|   |   |           |   |-- ResourceResponse.java
|   |   |           |   |-- ReservationRequest.java
|   |   |           |   |-- ReservationResponse.java
|   |   |           |
|   |   |           |-- entity
|   |   |           |   |-- User.java
|   |   |           |   |-- Role.java
|   |   |           |   |-- UserRole.java
|   |   |           |   |-- Resource.java
|   |   |           |   |-- Reservation.java
|   |   |           |   |-- ReservationStatus.java
|   |   |           |
|   |   |           |-- repository
|   |   |           |   |-- UserRepository.java
|   |   |           |   |-- RoleRepository.java
|   |   |           |   |-- UserRoleRepository.java
|   |   |           |   |-- ResourceRepository.java
|   |   |           |   |-- ReservationRepository.java
|   |   |           |
|   |   |           |-- security
|   |   |           |   |-- JwtService.java
|   |   |           |   |-- JwtAuthenticationFilter.java
|   |   |           |
|   |   |           |-- service
|   |   |           |   |-- AuthService.java
|   |   |           |   |-- ResourceService.java
|   |   |           |   |-- ReservationService.java
|   |   |           |
|   |   |           |-- serviceimpl
|   |   |               |-- AuthServiceImpl.java
|   |   |               |-- ResourceServiceImpl.java
|   |   |               |-- ReservationServiceImpl.java
|   |   |
|   |   |-- resources
|   |       |-- application.properties
|   |
|   |-- test
|       |-- java
|           |-- org
|               |-- techhub
|                   |-- ResourceBookingSystemApplicationTests.java
|
|-- pom.xml
|-- README.md
|-- .gitignore

## Prerequisites

Install the following:

* JDK 17 or later
* Maven
* MySQL
* Postman
* Eclipse, IntelliJ IDEA, or STS

Check Java version:

java -version

Check Maven version:

mvn -version

## Database Setup

Create the MySQL database:

CREATE DATABASE resource_booking_db;

Configure application.properties:

spring.application.name=Resource-Booking-System
spring.datasource.url=jdbc:mysql://localhost:3306/resourcebookingsystem
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080

Replace YOUR_PASSWORD with your MySQL password.

## JWT Configuration

Configure JWT according to JwtService:

jwt.secret=YOUR_SECRET_KEY
jwt.expiration=86400000

Do not commit real passwords or JWT secrets to GitHub.

## Run Application

Build the project:

mvn clean install

Run the project:

mvn spring-boot:run

Application URL:

http://localhost:8080

The application can also be started by running ResourceBookingSystemApplication.java from the IDE.

## Authentication

Register User:

POST /auth/register

Request:

{
"name": "Umesh",
"email": "[umesh@gmail.com](mailto:umesh@gmail.com)",
"password": "Password@123"
}

Login User:

POST /auth/login

Request:

{
"email": "[umesh@gmail.com](mailto:umesh@gmail.com)",
"password": "Password@123"
}

After successful login, the API returns a JWT token.

Use the token for protected APIs:

Authorization: Bearer <JWT_TOKEN>

## Roles

USER permissions:

* View resources
* Create reservations
* View own reservations
* View reservation by ID
* Cancel own reservation

ADMIN permissions:

* All USER permissions
* Create resources
* Update resources
* Delete resources
* View all reservations
* Confirm reservations
* Manage reservations

## API Endpoints

Authentication:

POST /auth/register - Public
POST /auth/login - Public

Resources:

GET /resources - USER / ADMIN
GET /resources/{id} - USER / ADMIN
POST /resources - ADMIN
PUT /resources/{id} - ADMIN
DELETE /resources/{id} - ADMIN

Reservations:

POST /reservations - USER / ADMIN
GET /reservations/my - USER / ADMIN
GET /reservations/{id} - USER / ADMIN
DELETE /reservations/{id} - USER / ADMIN
GET /reservations/admin/all - ADMIN
POST /reservations/admin/{id}/confirm - ADMIN

## Reservation Validation

The system validates:

* Start time and end time
* Start time must be before end time
* Resource must exist
* Resource must be available
* User must be authenticated
* Overlapping active reservations are prevented
* Cancelled reservations do not block new bookings

## Filtering, Sorting and Pagination

The reservation APIs support:

* Status filtering
* Minimum price
* Maximum price
* Sorting
* Pagination

Example:

/reservations/admin/all?status=CONFIRMED&minPrice=100&maxPrice=1000&page=0&size=10&sort=price,asc

## Swagger

Swagger UI:

http://localhost:8080/swagger-ui/index.html

Swagger can be used to register users, login, authorize with JWT, and test protected APIs.

## Postman Testing Flow

Start Application
Register USER
Login USER
Copy JWT Token
Set Bearer Token
Test Resource APIs
Create Reservation
View My Reservations
Cancel Reservation
Login ADMIN
Create Resource
Update Resource
Delete Resource
View All Reservations
Confirm Reservation

## Security

The application uses:

* Spring Security
* JWT Authentication
* Stateless Sessions
* BCrypt Password Hashing
* Role-Based Authorization
* JWT Authentication Filter
* Protected REST APIs

Public endpoints:

/auth/**
/swagger-ui/**
/swagger-ui.html
/v3/api-docs/**

## HTTP Status Codes

200 - Successful Request
201 - Resource Created
400 - Bad Request
401 - Unauthorized
403 - Forbidden
404 - Resource Not Found
409 - Conflict
500 - Internal Server Error

## Build

Create the production JAR:

mvn clean package

Run the JAR:

java -jar target/ResourceBookingSystem-*.jar

## Future Enhancements

* Email Notifications
* Payment Integration
* Calendar Integration
* Docker Deployment
* CI/CD Pipeline
* Redis Caching
* Audit Logging
* Advanced Search
* Admin Dashboard

## Author

Umesh Santosh Sawant

Project: Resource Booking System

Technologies: Java 17, Spring Boot, Spring Security, JWT, JPA/Hibernate, MySQL