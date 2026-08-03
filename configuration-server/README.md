# Configuration Server – Spring Cloud Config

Centralized Configuration Management for Microservices Architecture.

This project implements a Spring Cloud Config Server that externalizes configuration properties for all microservices in the system. It fetches configuration files from a remote Git repository and provides them dynamically to client services at runtime.

---

## 📌 Overview

In a microservices architecture, managing configuration across multiple services becomes complex. This Configuration Server:

- Centralizes all configuration files
- Fetches configuration from a Git repository
- Provides environment-specific configuration
- Supports dynamic refresh of properties
- Eliminates duplication across services

---

## 🏗 Architecture Role

Configuration Server acts as:

Microservices → Config Server → Git Repository

Each microservice connects to the Config Server on startup to retrieve its configuration.

---

## ⚙️ Tech Stack

- Java 17
- Spring Boot
- Spring Cloud Config Server
- Spring Cloud Netflix Eureka (optional)
- Maven
- Git (Remote Config Repository)

---

## 🚀 Setup Instructions

### 1️⃣ Create Project via Spring Initializer 

Use Spring Initializer with:

- Project: Maven
- Language: Java
- Spring Boot: 3.x
- Dependencies:
  - Spring Cloud Config Server
  - Eureka Discovery Client (if using service registry)

Or manually add: 

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>


### 2️⃣ Enable Config Server

In main class:

@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}


### 3️⃣ application.yml Configuration

server:
  port: 8888

spring:
  application:
    name: CONFIG-SERVER

  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-username/your-config-repo
          clone-on-start: true

---

## 📂 Config Repository Structure (Git)

Example Structure

hotel-service.yml
user-service.yml
rating-service.yml
hotel-service-dev.yml
hotel-service-prod.yml

Each microservice file must follow:

{application-name}.yml

---

## 🔌 How Client Microservice Connects

Add dependency:
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

In bootstrap.yml:
spring:
  application:
    name: USER-SERVICE
  config:
    import: optional:configserver:http://localhost:8888

---

## 🧪 Testing Configuration Fetch

Access:
http://localhost:8888/USER-SERVICE/default
This returns JSON configuration served from Git repository.

---

## 🔐 Why Config Server?

Avoids hardcoding configuration

Secure central management

Environment separation (dev / test / prod)

Scalable for enterprise systems

Industry-standard microservices practice

---

## 📈 Production Recommendations

Secure Git repository

Enable Spring Security

Use encrypted properties

Integrate with Vault or AWS Secrets Manager

Enable actuator refresh endpoint

---

