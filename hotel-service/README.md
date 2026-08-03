Hotel Service – Hotel Review Microservices System

Hotel Service is a Spring Boot microservice responsible for managing hotel information within the Hotel Review System.

The service exposes REST APIs that allow other services to retrieve hotel data associated with user ratings.

---

Role in the System

Hotel Service acts as a data provider service in the microservices architecture.

It performs the following responsibilities:

- Store hotel information
- Provide hotel details through REST APIs
- Serve hotel data requested by User Service

---

System Context

This service is part of a distributed microservices architecture.

Client
→ API Gateway
→ User Service
→ Rating Service
→ Hotel Service

Supporting infrastructure components include:

- Config Server
- Service Registry (Eureka)

---

Features

- Manage hotel data
- REST APIs for hotel retrieval
- Integration with User Service
- Service discovery using Eureka

---

Technology Stack

- Java
- Spring Boot
- Spring Cloud
- Maven
- REST APIs

---

Example API

GET /hotels/{hotelId}

Returns hotel details such as:

- Hotel name
- Location
- Description

---

Running the Service

Clone the repository:

git clone <https://github.com/NikStack20/Hotel-Review-System-HotelService>

Navigate to the project directory:

cd hotel-service

Run the application:

mvn spring-boot:run

---

License

This project is licensed under the MIT License.