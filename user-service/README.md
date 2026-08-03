# User Service

![Architecture](docs/images/user-service-architecture.png)

The User Service is responsible for managing user information and aggregating data from other microservices.

Instead of requiring clients to communicate with multiple services, the User Service retrieves user details, fetches ratings, obtains hotel information, and returns a unified response.

---

## Responsibilities

The service performs the following operations:

- Manage user information
- Retrieve ratings associated with a user
- Fetch hotel details from the Hotel Service
- Aggregate data from multiple services into a single response
- Expose REST APIs for client applications

---

## System Architecture

The User Service communicates with the following services:

```
Client
        │
        ▼
API Gateway
        │
        ▼
User Service
     │          │
     ▼          ▼
Rating Service  Hotel Service
```

Supporting Infrastructure:

- Service Registry (Eureka)
- Config Server
- API Gateway
- Zipkin
- MySQL Database

---

## Features

- User CRUD operations
- Service discovery using Eureka
- Centralized configuration using Spring Cloud Config
- Inter-service communication using OpenFeign
- OAuth2/JWT secured endpoints
- Distributed tracing with Zipkin
- Fault tolerance using Resilience4j
- Bulk hotel retrieval optimization
- Load tested using k6

---

## Engineering Decisions

### Aggregation Layer

The User Service acts as an aggregation layer for client requests.

Instead of exposing multiple downstream service calls to the client, it collects data from the Rating Service and Hotel Service, combines the responses, and returns a single API response.

---

### Bulk Hotel Retrieval

Initially, hotel information was fetched using one HTTP request per rating.

To reduce inter-service communication overhead, a bulk hotel retrieval endpoint was introduced.

This optimization significantly reduced the number of downstream HTTP calls during user aggregation requests and improved response efficiency under load.

---

## Resilience

The service uses Resilience4j to improve reliability while communicating with downstream services.

Implemented resilience patterns include:

- Retry
- Circuit Breaker
- Rate Limiter
- Fallback Methods

These mechanisms help maintain service availability when dependent services become slow or unavailable.

---

## Distributed Tracing

Distributed tracing is implemented using Zipkin.

Each request can be traced across:

- API Gateway
- User Service
- Rating Service
- Hotel Service

This helps visualize request flow, identify latency, and analyze service dependencies.

---

## Performance Testing

The service was evaluated using **k6** to observe its behavior under concurrent requests.

Testing focused on:

- Request latency
- Response success rate
- Downstream service communication
- Resilience during service failures
- Aggregation performance

Performance analysis was also used to identify optimization opportunities within inter-service communication.

---

## Technology Stack

- Java
- Spring Boot
- Spring Cloud
- Spring Data JPA
- OpenFeign
- Resilience4j
- OAuth2 Resource Server
- MySQL
- Zipkin
- k6
- Maven

---

## Running the Service

Clone the repository:

```bash
git clone https://github.com/NikStack20/Hotel-Review-System-UserService
```

Navigate to the project:

```bash
cd UserService
```

Run the application:

```bash
mvn spring-boot:run
```

---

## Future Improvements

Planned enhancements include:

- Docker containerization
- Docker Compose support
- Prometheus metrics
- Grafana dashboards
- Redis caching
- Kubernetes deployment

---

## License

This project is licensed under the MIT License.