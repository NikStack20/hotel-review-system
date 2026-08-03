<div align="center">

# Configuration Architecture

### A Production-Grade Blueprint for Externalized Config in Spring Boot Microservices

`Spring Boot 3.5.x` · `Java 17` · `Spring Cloud Config` · `Eureka` · `OpenFeign` · `Resilience4j` · `Zipkin` · `OAuth2 (Okta)`

*"One artifact. Many environments. Zero hardcoded truths."*

</div>

---

## Table of Contents

1. [Why This Document Exists](#why-this-document-exists)
2. [The Core Principle](#the-core-principle)
3. [Should dev/prod YAMLs Live Inside Each Service?](#1--should-devprod-yamls-live-inside-each-service)
4. [Activating Spring Profiles the Right Way](#2--activating-spring-profiles-the-right-way)
5. [Environment Variables: Native vs. Dotenv](#3--environment-variables-native-vs-dotenv)
6. [Bootstrapping the Config Server Itself](#4--bootstrapping-the-config-server-itself)
7. [Architecture Review — Findings](#5--architecture-review--findings)
8. [The Target Project Structure](#6--the-target-project-structure)
9. [Golden Rules Cheat Sheet](#golden-rules-cheat-sheet)
10. [Roadmap](#roadmap)

---

## Why This Document Exists

Every microservice system eventually asks the same question:

> *"Where does the truth about my configuration actually live?"*

Get this wrong, and you end up with **three versions of the truth** — one in the codebase, one in a `.env` file, one in a config repo — quietly disagreeing with each other until an incident forces you to find out which one production was actually using.

This document is the single source of truth for **how config flows through this system**, from a developer's laptop all the way to a Kubernetes pod in production.

---

## The Core Principle

```
┌─────────────────────────────────────────────────────────────┐
│                                                               │
│   ONE IMAGE.  ONE ARTIFACT.  ONE BUILD.                      │
│                                                               │
│   Environment is a RUNTIME DECISION —                       │
│   never a BUILD-TIME one.                                    │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

If you can't take the exact same Docker image and promote it unchanged from `dev → staging → prod`, the architecture has a leak somewhere. Everything below exists to plug those leaks.

---

## 1 — Should dev/prod YAMLs Live Inside Each Service?

**No.** Each microservice keeps exactly **one** local file — and that file only knows how to *find* the Config Server, nothing more.

```yaml
# user-service/src/main/resources/application.yml
spring:
  application:
    name: user-service
  config:
    import: "configserver:"
  cloud:
    config:
      uri: ${CONFIG_SERVER_URI:http://localhost:8888}
      fail-fast: true
      retry:
        max-attempts: 6
        initial-interval: 1500
```

```
┌───────────────────────────┐        ┌────────────────────────────┐
│   LOCAL application.yml   │        │     CONFIG REPOSITORY       │
│   "How do I bootstrap?"   │──────▶ │  "What is my behavior?"     │
│                           │        │                              │
│   • Config Server URI     │        │  • DB credentials           │
│   • fail-fast / retry     │        │  • Feature flags            │
│                           │        │  • Resilience4j thresholds  │
│                           │        │  • Zipkin sampling rate      │
└───────────────────────────┘        └────────────────────────────┘
```

`application-dev.yml` / `application-prod.yml` sitting inside a service is a relic of the pre-Config-Server era. Keeping both creates **two competing resolution paths** for the same property — a guaranteed source of "why is prod using the dev database" incidents.

**Verdict:** Delete every `application-{profile}.yml` from every service module. Migrate their contents into the Config Repo if not already mirrored there.

---

## 2 — Activating Spring Profiles the Right Way

`SPRING_PROFILES_ACTIVE` is **injected**, never written into a committed file.

```
     IntelliJ Run Config          Docker Compose            Kubernetes                 CI/CD
   ┌───────────────────┐      ┌───────────────────┐    ┌───────────────────┐    ┌───────────────────┐
   │ Env Vars field     │      │ environment:      │    │ ConfigMap →        │    │ Pipeline secret /  │
   │ SPRING_PROFILES_   │─────▶│  SPRING_PROFILES_ │───▶│  pod env            │───▶│  variable injected │
   │ ACTIVE=dev          │      │  ACTIVE=${PROFILE}│    │  SPRING_PROFILES_  │    │  at deploy step     │
   └───────────────────┘      └───────────────────┘    │  ACTIVE=prod        │    └───────────────────┘
                                                          └───────────────────┘
```

A missing `SPRING_PROFILES_ACTIVE` in production should **crash the service loudly** — never silently fall back to a dev default. `fail-fast: true` on the Config Server client enforces exactly this.

---

## 3 — Environment Variables: Native vs. Dotenv

```
                         WHO OWNS THE .env FILE?

     ┌─────────────┐        ┌──────────────────┐        ┌─────────────────┐
     │  .env file    │──────▶│  Docker Compose /  │──────▶│  Spring Boot sees │
     │  (dev only,   │       │  Kubernetes         │       │  plain OS env      │
     │  gitignored)  │       │  (owns resolution)  │       │  vars — nothing     │
     └─────────────┘        └──────────────────┘        │  extra needed       │
                                                            └─────────────────┘

     ❌ NOT THIS:
     .env file ──▶ [spring-dotenv library inside the JAR] ──▶ Spring Boot
                    (adds a runtime dependency that behaves
                     differently based on file presence — a smell)
```

**Answer: native environment variables, owned by the orchestrator — not a dotenv dependency.**

| Environment | Who resolves the variables |
|---|---|
| Local Dev | IDE run config / shell export |
| Docker Compose | Compose's native `.env` support |
| Kubernetes | `ConfigMap` (plain) + `Secret` (sensitive) |
| CI/CD | Pipeline secret store |

Spring Boot already does relaxed binding against OS environment variables out of the box — introducing a dotenv library inside the artifact solves a problem the platform already solves, while quietly reintroducing environment drift between `java -jar` and "runs in a container."

---

## 4 — Bootstrapping the Config Server Itself

Config Server is the one service allowed to know an external Git URL — and even that URL is environment-dependent.

```yaml
# config-server/src/main/resources/application.yml
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

```
   Local Dev            Docker Compose            Kubernetes
 ┌───────────────┐    ┌───────────────────┐    ┌──────────────────────┐
 │ shell env vars │    │ environment: block │    │ ConfigMap (URI/branch)│
 │ or file:// path│    │ from root .env      │    │ + Secret (Git token)  │
 └───────────────┘    └───────────────────┘    └──────────────────────┘
```

Git authentication in production uses a **read-only deploy key or machine-user token** — never a personal access token tied to an individual.

---

## 5 — Architecture Review — Findings

| # | Finding | Severity | Action |
|---|---|---|---|
| 5.1 | Duplicated config: local `dev`/`prod` YAMLs vs. Config Repo | 🔴 High | Delete local per-profile files |
| 5.2 | Risk of building environment-specific Docker images | 🔴 High | Enforce single image, runtime-injected env |
| 5.3 | Cross-service database access | 🟠 Watch | Confirm strict DB-per-service isolation |
| 5.4 | Missing `fail-fast` / `retry` on Config clients | 🟡 Medium | Add to every service's bootstrap config |
| 5.5 | Config Server discovery via Eureka (chicken-and-egg) | 🟠 Watch | Confirm static URI is used, not `lb://` |
| 5.6 | Secret placeholder consistency across services | 🟡 Medium | Audit for any stray hardcoded credential |
| 5.7 | Partial externalization of Zipkin/Resilience4j config | 🟡 Medium | Fully centralize in Config Repo |
| 5.8 | `git subtree` history integrity | 🟢 Low | Verify with `git log --follow` per service |

---

## 6 — The Target Project Structure

```
project-monorepo/
├── api-gateway/
│   ├── src/
│   ├── Dockerfile
│   └── application.yml            # bootstrap only
├── config-server/
│   ├── src/
│   ├── Dockerfile
│   └── application.yml
├── service-registry/
│   ├── src/
│   ├── Dockerfile
│   └── application.yml
├── user-service/
│   ├── src/
│   ├── Dockerfile
│   └── application.yml
├── hotel-service/
│   ├── src/
│   ├── Dockerfile
│   └── application.yml
├── rating-service/
│   ├── src/
│   ├── Dockerfile
│   └── application.yml
│
├── docker-compose.yml
├── docker-compose.override.yml     # local dev overrides
├── .env.example
├── .env                            # gitignored
│
├── k8s/
│   ├── base/
│   │   ├── user-service/
│   │   │   ├── deployment.yaml
│   │   │   ├── service.yaml
│   │   │   └── configmap.yaml
│   │   ├── hotel-service/
│   │   ├── rating-service/
│   │   ├── api-gateway/
│   │   ├── config-server/
│   │   └── service-registry/
│   └── overlays/
│       ├── dev/kustomization.yaml
│       ├── staging/kustomization.yaml
│       └── prod/kustomization.yaml
│
└── README.md
```

```
config-repo/                        (separate Git repository)
├── application.yml                 # truly global shared defaults
├── user-service.yml
├── user-service-dev.yml
├── user-service-prod.yml
├── hotel-service.yml
├── hotel-service-dev.yml
├── hotel-service-prod.yml
├── rating-service.yml
├── rating-service-dev.yml
├── rating-service-prod.yml
├── api-gateway.yml
├── api-gateway-dev.yml
├── api-gateway-prod.yml
└── config-server.yml
```

**Kubernetes note:** use **Kustomize** — one `base/` shared across environments, thin `overlays/` patching only what actually differs (replica count, resource limits, env-specific ConfigMap refs). This is the standard nearly every enterprise Spring Boot-on-K8s team converges on.

---

## Golden Rules Cheat Sheet

```
┌───────────────────────────────────────────────────────────────────┐
│ 1. One image per service. Environment is injected, never baked.  │
│ 2. Local application.yml = bootstrap only. No business config.   │
│ 3. Config Repo = single source of truth for behavior.             │
│ 4. SPRING_PROFILES_ACTIVE is never hardcoded, anywhere.           │
│ 5. Secrets live in Secret objects / Vault — never in Git.         │
│ 6. fail-fast: true. A missing config value should be LOUD.        │
│ 7. Config Server discovers Git via static URI — not via Eureka.  │
│ 8. Same env-var contract across Compose and Kubernetes.          │
└───────────────────────────────────────────────────────────────────┘
```

---

## Roadmap

- [x] Migrate to Git Monorepo (history preserved via `git subtree`)
- [x] Externalize configuration to a dedicated Config Repository
- [x] Remove hardcoded secrets — placeholder-only config committed
- [ ] Remove residual `application-dev.yml` / `application-prod.yml` from services
- [ ] Add `fail-fast` + `retry` to every service's Config Client bootstrap
- [ ] Containerize all services (Docker) — single image, runtime env injection
- [ ] Author `docker-compose.yml` with `.env`-driven variable resolution
- [ ] Author Kubernetes `base/` manifests + `dev/staging/prod` Kustomize overlays
- [ ] Migrate secrets to a proper secrets manager (Vault / External Secrets Operator)

---

<div align="center">

*Built for teams who'd rather debug a business problem than a missing environment variable.*

</div>
