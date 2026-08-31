<div align="center">

# Deployment & Configuration Architecture

### Current Runtime State, Environment Strategy, and Production Readiness

`Spring Boot 3.5.x` · `Java 17` · `Spring Cloud Config` · `Eureka` · `OpenFeign` · `Resilience4j` · `Zipkin` · `OAuth2 (Okta)` · `Docker Compose`

*"One artifact. Runtime configuration. Evidence before assumptions."*

</div>

---

## Table of Contents

1. [Purpose](#purpose)
2. [Current Verified Deployment State](#current-verified-deployment-state)
3. [Current Docker Compose Topology](#current-docker-compose-topology)
4. [Configuration Architecture](#configuration-architecture)
5. [Environment Variables and Profiles](#environment-variables-and-profiles)
6. [Service Startup Dependencies](#service-startup-dependencies)
7. [Networking and Ports](#networking-and-ports)
8. [Secrets and Credentials](#secrets-and-credentials)
9. [Operational Verification](#operational-verification)
10. [Production Readiness Assessment](#production-readiness-assessment)
11. [Known Limitations and Follow-up Work](#known-limitations-and-follow-up-work)
12. [Target Production Architecture](#target-production-architecture)
13. [Deployment Checklist](#deployment-checklist)

---

## Purpose

This document is the deployment-oriented source of truth for the Hotel Review System.

It distinguishes between:

- **verified current behaviour**,
- **architecture decisions**,
- **deployment design**, and
- **future production work**.

The project is currently capable of being brought up as a multi-container Docker Compose environment. The current Compose configuration builds the application services from their local Dockerfiles, connects infrastructure and Spring services through a shared Docker network, and injects runtime configuration through environment variables.

This document must not describe planned Kubernetes or production infrastructure as if it has already been deployed.

---

# Current Verified Deployment State

## Runtime Snapshot

The current Docker Compose environment was verified with all application and infrastructure containers running:

| Component | Container | Current State | Host Port |
|---|---|---|---:|
| API Gateway | `hrs-api-gateway` | Running | `7053` |
| Config Server | `hrs-config-server` | Running / healthy | `7054` |
| Service Registry | `hrs-service-registry` | Running / healthy | `8761` |
| User Service | `hrs-user-service` | Running | internal `7052` |
| Hotel Service | `hrs-hotel-service` | Running | internal `7050` |
| Rating Service | `hrs-rating-service` | Running | internal `7051` |
| MySQL | `hrs-mysql` | Running / healthy | `3308` |
| PostgreSQL | `hrs-postgres` | Running / healthy | `5434` |
| MongoDB | `hrs-mongodb` | Running / healthy | `27019` |
| Zipkin | `hrs-zipkin` | Running / healthy | `9411` |

The application services are intentionally exposed primarily through the API Gateway. Internal Spring services communicate over the Docker network rather than requiring host-port publication.

## Important Scope Clarification

The verified environment is a **Docker Compose deployment environment**, not a production Kubernetes deployment.

The current Compose file explicitly sets:

```yaml
SPRING_PROFILES_ACTIVE: dev
```

for the User, Hotel, Rating, and API Gateway services.

Therefore:

> The current environment proves that the containerized **dev/integration topology works**. It must not be labelled as a production deployment until production configuration, secrets, image promotion, and deployment infrastructure are separately validated.

---

# Current Docker Compose Topology

The current Compose architecture is divided into four logical groups.

```text
                         Client
                           |
                           v
                    +--------------+
                    | API Gateway  |
                    |    :7053     |
                    +------+-------+
                           |
          +----------------+----------------+
          |                |                |
          v                v                v
   +-------------+  +-------------+  +-------------+
   | User Service|  |Hotel Service|  |Rating Service|
   |    7052     |  |    7050     |  |    7051     |
   +------+------+  +------+------+  +------+------+
          |                |                |
          v                v                v
       MySQL           PostgreSQL        MongoDB
       :3306              :5432          :27017

                    Platform Layer
             +-------------------------+
             | Config Server :7054     |
             | Eureka       :8761      |
             | Zipkin       :9411      |
             +-------------------------+
```

All containers join the shared:

```text
hrs-network
```

Docker's internal DNS therefore allows services to use names such as:

```text
mysql
postgres
mongodb
config-server
service-registry
zipkin
```

instead of `localhost`.

---

# Configuration Architecture

## Configuration Ownership

The system follows a centralised configuration model:

```text
Service application.yml
        |
        | bootstrap information
        v
 Config Server
        |
        v
 External Config Repository
        |
        +--> service behaviour
        +--> database configuration
        +--> tracing configuration
        +--> resilience configuration
        +--> environment-specific values
```

A service's local `application.yml` should contain only the information required to bootstrap the service and locate the Config Server.

The external Config Repository remains the intended source of truth for service behaviour.

## Config Server Bootstrap

The Config Server obtains its Git repository location from runtime configuration:

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: ${CONFIG_REPO_URI}
          default-label: ${CONFIG_REPO_BRANCH:main}
          clone-on-start: true
          force-pull: true
```

The Config Server itself is therefore environment-aware without requiring environment-specific application binaries.

## Config Server and Eureka

The Config Server uses a static Config Server URI for client bootstrap.

This avoids the circular dependency:

```text
Config Client
    |
    +--> Eureka?
           |
           +--> Config Server?
```

Instead:

```text
Config Client
    |
    +--> CONFIG_SERVER_URL
             |
             v
        Config Server
             |
             v
          Git Repo
```

Eureka remains the service-discovery mechanism for the application services after bootstrap.

---

# Environment Variables and Profiles

## Current Compose Behaviour

The current Compose file injects runtime values for:

- database URLs and credentials,
- Config Server location,
- Eureka location,
- Zipkin endpoint,
- Okta configuration,
- Spring profile selection,
- Config Repository location and branch.

Examples:

```yaml
CONFIG_SERVER_URL: http://config-server:7054
EUREKA_SERVER_URL: http://service-registry:8761/eureka/
ZIPKIN_ENDPOINT: http://zipkin:9411/api/v2/spans
```

This is the correct pattern for container-to-container communication.

## Profile Status

The current Compose deployment explicitly uses:

```text
SPRING_PROFILES_ACTIVE=dev
```

This is acceptable for the current development/integration environment.

For production, the value must be supplied by the deployment environment rather than being permanently hardcoded to `dev` in the production deployment definition.

Recommended model:

```text
Local development
    -> dev

Staging
    -> staging

Production
    -> prod
```

The same application artifact should be promoted between environments while environment-specific configuration is injected at runtime.

---

# Service Startup Dependencies

The Compose file uses health-based startup dependencies for infrastructure and platform components.

Examples include:

```yaml
depends_on:
  mysql:
    condition: service_healthy
```

and:

```yaml
depends_on:
  config-server:
    condition: service_healthy
```

This is stronger than simply starting containers in a fixed order.

The intended dependency chain is:

```text
Databases
   |
   v
Service Registry
   |
   v
Config Server
   |
   v
Business Services
   |
   v
API Gateway
```

The Gateway additionally waits for the business services to be started.

## Important Limitation

`service_started` confirms that Docker has started the container; it does not prove that the application is fully ready to receive traffic.

For production-grade orchestration, readiness checks should be used consistently at the application level.

---

# Networking and Ports

## Host-facing ports

| Component | Host Port | Container Port |
|---|---:|---:|
| API Gateway | `7053` | `7053` |
| Config Server | `7054` | `7054` |
| Eureka | `8761` | `8761` |
| MySQL | `3308` | `3306` |
| PostgreSQL | `5434` | `5432` |
| MongoDB | `27019` | `27017` |
| Zipkin | `9411` | `9411` |

The application services themselves are not published directly to the host in the current Compose topology.

That is desirable for the current architecture because client traffic should enter through the Gateway.

## Internal service communication

Inside `hrs-network`, containers use Docker DNS:

```text
mysql:3306
postgres:5432
mongodb:27017
config-server:7054
service-registry:8761
zipkin:9411
```

The host mappings such as `3308`, `5434`, and `27019` are for host-side access and are not the addresses application containers should use.

---

# Secrets and Credentials

Sensitive configuration must never be committed as real values.

The repository should contain only:

- placeholders,
- `.env.example`,
- configuration templates,
- documentation.

Actual values belong in:

```text
Local:
    .env / shell / IDE environment

CI/CD:
    pipeline secret store

Kubernetes:
    Secret / external secret manager

Production:
    Vault / cloud secret manager / equivalent
```

The following must never be committed:

```text
OKTA_CLIENT_SECRET
database passwords
Git access tokens
private keys
personal access tokens
production credentials
```

Production Git access for the Config Repository should use a dedicated machine identity or read-only deploy credential rather than a personal token.

---

# Operational Verification

## Current Compose verification

The following command is the primary first-level deployment check:

```bash
docker compose ps
```

Expected result:

```text
All required containers -> Up
Infrastructure healthchecks -> healthy
```

## Service Registry verification

Open:

```text
http://localhost:8761
```

The Eureka dashboard should show the registered application services.

Expected registered services include:

```text
API-GATEWAY
USER-SERVICE
HOTEL-SERVICE
RATING-SERVICE
```

## Config Server verification

The Config Server is exposed on:

```text
http://localhost:7054
```

Its Docker healthcheck targets:

```text
/actuator/health
```

## Zipkin verification

Zipkin is exposed on:

```text
http://localhost:9411
```

The system sends tracing data through:

```text
http://zipkin:9411/api/v2/spans
```

from inside the Compose network.

## API Gateway verification

The public application entry point for the current Compose environment is:

```text
http://localhost:7053
```

A successful end-to-end API request through the Gateway confirms that the request can traverse the relevant routing and service-discovery path.

For example, the verified `/users` integration path has been exercised through the Gateway successfully.

This is an integration verification, not a load-test result.

---

# Production Readiness Assessment

## Completed

- [x] Git monorepo established
- [x] External configuration architecture established
- [x] Secrets removed from committed configuration in favour of placeholders
- [x] Dockerfiles available for application services
- [x] Docker Compose deployment authored
- [x] Shared Docker network configured
- [x] MySQL containerised
- [x] PostgreSQL containerised
- [x] MongoDB containerised
- [x] Eureka containerised
- [x] Config Server containerised
- [x] Zipkin containerised
- [x] API Gateway containerised
- [x] User, Hotel, and Rating services containerised
- [x] Infrastructure healthchecks configured
- [x] Compose environment successfully brought up
- [x] Service registration verified
- [x] End-to-end API integration verified

## Not yet production-complete

- [ ] Remove hardcoded `SPRING_PROFILES_ACTIVE: dev` from the production deployment path
- [ ] Establish immutable image build and promotion workflow
- [ ] Separate build from deployment
- [ ] Add staging and production deployment definitions
- [ ] Move production secrets to a dedicated secret manager
- [ ] Add production-grade readiness/liveness strategy
- [ ] Add resource limits and requests
- [ ] Add production observability/alerting policy
- [ ] Define persistent storage and backup strategy
- [ ] Define database migration strategy
- [ ] Define rollback procedure
- [ ] Add CI/CD deployment pipeline
- [ ] Validate production configuration against the external Config Repository
- [ ] Author Kubernetes manifests/Kustomize overlays if Kubernetes is selected as the production platform

---

# Known Limitations and Follow-up Work

## 1. Compose currently builds services

The current Compose configuration uses:

```yaml
build:
  context: ./service-name
```

This is perfectly valid for local development and integration testing.

For a production promotion model, the preferred flow is:

```text
Source
  |
  v
CI build
  |
  v
Immutable image
  |
  v
Registry
  |
  +--> staging
  |
  +--> production
```

The production environment should consume the already-built image rather than rebuilding application source during deployment.

## 2. Development profile is explicit in Compose

The current Compose file sets:

```yaml
SPRING_PROFILES_ACTIVE: dev
```

That should remain clearly scoped to the development Compose environment.

Production must inject:

```text
SPRING_PROFILES_ACTIVE=prod
```

through its deployment mechanism.

## 3. Healthchecks are strongest for infrastructure/platform services

The current Compose setup has healthchecks for databases, Eureka, and Config Server.

The business services should eventually expose and use explicit readiness/liveness checks for orchestration.

## 4. Kubernetes is a target, not a completed deployment

The intended Kubernetes structure is:

```text
k8s/
├── base/
│   ├── user-service/
│   ├── hotel-service/
│   ├── rating-service/
│   ├── api-gateway/
│   ├── config-server/
│   └── service-registry/
│
└── overlays/
    ├── dev/
    ├── staging/
    └── prod/
```

This must remain documented as planned until the manifests are actually implemented and validated.

---

# Target Production Architecture

```text
                         Internet / Client
                                |
                                v
                       +----------------+
                       |  API Gateway   |
                       +-------+--------+
                               |
                 +-------------+-------------+
                 |             |             |
                 v             v             v
          User Service   Hotel Service  Rating Service
                 |             |             |
                 v             v             v
              MySQL        PostgreSQL      MongoDB

                     Platform / Control Plane
              +-------------------------------+
              | Config Server                 |
              | Eureka                        |
              | Zipkin / Observability        |
              +-------------------------------+

                    Configuration Plane
                              |
                              v
                     Config Git Repository
```

The production deployment should follow these principles:

1. **One build per service version.**
2. **Runtime environment selection.**
3. **No secrets in Git.**
4. **Config Server bootstrap must not depend on Eureka.**
5. **Service discovery handled by Eureka where required.**
6. **Externalized service configuration.**
7. **Gateway as the primary external application entry point.**
8. **Independent database ownership per service.**
9. **Health-based deployment and rollback.**
10. **Observable runtime behaviour through logs, metrics, and tracing.**

---

# Deployment Checklist

## Before starting

```bash
docker compose config
```

Verify that:

- required environment variables are available,
- no real secrets are being committed,
- service names match internal DNS references.

## Start

```bash
docker compose up -d --build
```

## Verify

```bash
docker compose ps
```

Then verify:

```text
Eureka
Config Server
Gateway
service registration
database health
Zipkin
end-to-end API request
```

## Stop

```bash
docker compose down
```

Persistent database volumes are intentionally retained unless explicitly removed.

To remove volumes as well:

```bash
docker compose down -v
```

Use this carefully because it deletes the Compose-managed database data.

---

# Final Architecture Position

The project has moved beyond the "not yet containerized" stage.

The current evidence establishes a working Docker Compose integration environment containing the complete application topology and supporting infrastructure.

The next architectural step is **not another basic containerization pass**.

The next step is to harden the existing deployment model:

```text
CURRENT
Docker Compose
    +
runtime configuration
    +
service discovery
    +
external config
    +
healthchecks
    +
verified integration
          |
          v
NEXT
immutable images
    +
staging/prod profiles
    +
secret management
    +
readiness/liveness
    +
CI/CD
    +
production deployment
```

The distinction is important: **the system is containerized and operational in Compose, but production deployment hardening is still unfinished.**

---

<div align="center">

*Deployment documentation should describe what the system actually does — not what we hope it does.*

</div>
