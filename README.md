# ⭐ Rating Service

Rating Service is a microservice responsible for managing and handling user ratings for hotels within the distributed microservices architecture.

This service is part of a larger system consisting of:
- User Service
- Hotel Service
- API Gateway
- Config Server
- Eureka Server

---

## 🚀 Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Spring Cloud (Eureka Client)
- RESTful APIs
- Maven

---

## 📌 Features

- Create Rating
- Get Ratings by User ID
- Get Ratings by Hotel ID
- Microservice Communication
- Service Discovery via Eureka
- Centralized Configuration Support

---

## 🏗️ Architecture

This service follows a microservices architecture pattern:

- Registers with Eureka Server
- Communicates via REST APIs
- Externalized configuration using Config Server
- Database persistence using MySQL

---

## 📂 API Endpoints

### ➤ Create Rating

POST /ratings

### ➤ Get Ratings by User

GET /ratings/users/{userId}

### ➤ Get Ratings by Hotel

GET /ratings/hotels/{hotelId}

---

## ⚙️ Configuration

Example `application.yml` configuration:

```yaml
server:
  port: 7051

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/microservices
    username: root
    password: your_password

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka


🐳 Docker Support
To build Docker image:
docker build -t rating-service .

Run container:
docker run -p 7051:7051 rating-service

🔗 Service Registration

After startup, verify service registration:
http://localhost:8761


🧪 Running the Application

mvn clean install
mvn spring-boot:run


📖 Future Enhancements

Resilience4j integration
Logging & Monitoring
API Documentation with Swagger
Unit & Integration Testing
CI/CD Pipeline


👨‍💻 Author
Developed as part of a Microservices learning project --- [NikStack20]


