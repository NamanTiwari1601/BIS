# Billing and Inventory System (BIS)

A backend system for managing billing and inventory operations with real transactional integrity — built to model the kind of correctness and reliability concerns that come up in production e-commerce/retail systems.

## Why this project

Most CRUD-style projects skip the hard part: what happens when two people try to buy the last item in stock, or a payment fails halfway through? BIS is built around getting that right — service-layer validation, server-side pricing (never trust the client), and transactional rollback on failure.

## Features

- **Billing** — create and manage bills (`BillModel`) composed of multiple line items (`BillItemModel`)
- **Stock validation** — inventory checked and decremented at the service layer before a bill is finalized, preventing overselling
- **Server-side pricing** — prices are calculated and validated on the backend, not trusted from client input
- **Transactional rollback** — if any part of a billing operation fails, the entire transaction rolls back to keep inventory and billing data consistent
- **Clean DTO boundaries** — request/response DTOs separate API contracts from persistence entities

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Framework | Spring Boot |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Containerization | Docker |
| CI/CD | GitHub Actions |
| Testing | JUnit 5, Mockito *(Testcontainers integration in progress)* |
| Resilience | Resilience4j *(exploring circuit-breaker patterns)* |

## Architecture

```
Client
  │
  ▼
Controller Layer  ──►  DTOs (request/response contracts)
  │
  ▼
Service Layer  ──►  Stock validation, pricing logic, transaction boundaries
  │
  ▼
Repository Layer (Spring Data JPA)
  │
  ▼
PostgreSQL
```

## Key Engineering Decisions

- **Wrapper types over primitives** on entity fields — avoids Jackson null-deserialization errors when optional fields are missing from incoming JSON, at the cost of explicit null-handling in business logic.
- **Validation at the service layer, not the controller** — keeps stock/pricing rules centralized and testable independent of the HTTP layer.
- **`@Transactional` boundaries around multi-step billing operations** — ensures partial failures (e.g. stock decrement succeeds but bill save fails) don't leave inconsistent data.

## Running Locally

```bash
# Clone
git clone https://github.com/NamanTiwari1601/BIS.git
cd BIS

# Run with Docker
docker-compose up --build

# Or run locally with Maven
./mvnw spring-boot:run
```

*(Update the above with your actual run commands/ports/env vars.)*

## CI/CD

GitHub Actions pipeline runs on every push — builds the project and runs the test suite before merge.

## Roadmap

- [ ] Redis caching layer for frequently accessed inventory data
- [ ] Rate limiting on public-facing APIs
- [ ] Observability: metrics + structured logging (Micrometer/Prometheus)
- [ ] Load testing with documented before/after throughput numbers
- [ ] Testcontainers-based integration tests against a real PostgreSQL instance

## Author

Built by [Naman Tiwari](https://github.com/NamanTiwari1601) as a resume/skills-strengthening project focused on backend correctness and production-style engineering practices.
