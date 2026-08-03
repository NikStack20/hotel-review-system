# HotelReview System – API Gateway

## Overview

The API Gateway serves as the centralized entry point for the HotelReview System microservices architecture. It is responsible for routing external client requests to appropriate backend services while enforcing authentication, security policies, and traffic control.

This gateway abstracts internal service complexity and ensures scalable, secure, and maintainable communication between distributed microservices.

---

## Purpose

In a distributed system, individual services handle separate business domains such as:

- User Service
- Hotel Service
- Review Service

The API Gateway consolidates access to these services and provides:

- A single public entry point
- Centralized authentication and authorization
- Service discovery integration
- Request filtering and logging
- Cross-Origin Resource Sharing (CORS) control

---

## Core Responsibilities

- Centralized routing using Spring Cloud Gateway
- JWT token validation and authentication enforcement
- Integration with Eureka Service Discovery
- Load-balanced routing to downstream services
- Global request filtering
- CORS configuration
- Optional rate limiting support

---

## Technology Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Cloud Gateway**
- **Spring Security**
- **JWT Authentication**
- **Netflix Eureka**
- **Maven**

---

## System Architecture Flow

Client Request
↓
API Gateway
↓
Authentication Filter
↓
Service Discovery (Eureka)
↓
Target Microservice
↓
Response Returned to Client



---

## Project Initialization

The API Gateway can be generated using Spring Initializer:

🔗 https://start.spring.io/

### Project Configuration

- Project: Maven
- Language: Java
- Spring Boot: 3.x (latest stable)
- Packaging: Jar
- Java Version: 17+

### Required Dependencies

- Spring Cloud Gateway
- Spring Web
- Spring Security
- Eureka Discovery Client
- Spring Boot Actuator (recommended)
- Lombok (optional)

After generating the project:

1. Import into your preferred IDE.
2. Configure `application.yml` for routing rules.
3. Enable Eureka client configuration.
4. Implement JWT authentication filter.
5. Start Eureka Server before launching the Gateway.

---

## Sample Route Configuration

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/users/**

        - id: hotel-service
          uri: lb://HOTEL-SERVICE
          predicates:
            - Path=/hotels/**

        - id: review-service
          uri: lb://REVIEW-SERVICE
          predicates:
            - Path=/reviews/**



Running the Application

Ensure the following services are running:

Eureka Server

Target Microservices

API Gateway


Start the Gateway using:

mvn spring-boot:run


Default Access Example:
http://localhost:8080

Production Recommendations

For enterprise deployment:

Enable HTTPS

Configure rate limiting

Add centralized logging (ELK or similar)

Enable Actuator monitoring endpoints

Containerize using Docker

Deploy behind load balancer or reverse proxy


Author

Nikhil Chauhan
Backend Developer
GitHub: https://github.com/NikStack20


