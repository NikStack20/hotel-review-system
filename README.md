User Service

User Service is a microservice responsible for managing user information and aggregating related data from other
services within the system.

It retrieves user data and enriches it with ratings and hotel details by communicating with other microservices.

---

Role in the System

User Service acts as an aggregation layer.

It performs the following tasks:

- Fetch user data from the database
- Retrieve user ratings from the Rating Service
- Fetch hotel details from the Hotel Service
- Combine responses and return a unified result to the client

---

Service Dependencies

User Service communicates with the following components in the system:

- API Gateway – handles external client requests
- Service Registry – enables service discovery
- Config Server – provides centralized configuration
- Rating Service – provides rating information for users
- Hotel Service – provides hotel details

---

Resilience

To ensure stability during downstream failures, the service implements resilience patterns using Resilience4j:

- Retry
- Rate Limiter
- Circuit Breaker
- Fallback methods

These mechanisms protect the system from cascading failures and help maintain service availability.

---

Technology Stack

- Java
- Spring Boot
- Spring Cloud
- Resilience4j
- REST APIs
- Maven
- MySQL
- Apache JMeter

---

Running the Service

Clone the repository:

git clone <https://github.com/NikStack20/Hotel-Review-System-UserService>

Navigate to the project directory:

cd user-service

Run the application:

mvn spring-boot:run

---

Load Testing

Basic load testing was performed using Apache JMeter to observe service behavior under concurrent requests and when
downstream services fail.

---

License

This project is licensed under the MIT License.