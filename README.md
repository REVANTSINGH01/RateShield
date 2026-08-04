# RateShield

> A distributed rate limiting microservice built to protect REST APIs from excessive traffic and ensure fair resource usage.

---

## Overview

RateShield is a backend infrastructure project that provides a reusable and scalable rate limiting service for modern applications.

Instead of implementing rate limiting separately in every application, RateShield acts as a standalone service that can be integrated with any backend to protect APIs from abuse while maintaining consistent performance.

The project focuses on building a production-style rate limiter using industry-standard algorithms and distributed system principles.

---

## Why RateShield?

Modern APIs are exposed to thousands of requests every second. Without proper request control, applications can suffer from:

- High server load
- Increased response times
- Database overload
- Resource exhaustion
- Service downtime

RateShield helps prevent these issues by controlling how quickly clients can consume server resources.

---

## Planned Features

- Token Bucket algorithm
- Sliding Window algorithm
- Fixed Window algorithm
- Leaky Bucket algorithm
- Distributed rate limiting
- Per-user rate limiting
- Per-IP rate limiting
- Per-endpoint rate limiting
- Configurable rate limiting policies
- HTTP 429 responses with Retry-After support
- Redis-backed shared state
- REST APIs for easy integration
- Metrics and monitoring
- Docker support

---

## Planned Architecture

```text
                    Client
                       │
                       ▼
              Application Backend
                       │
                       ▼
                  RateShield
                       │
        ┌──────────────┴──────────────┐
        │                             │
        ▼                             ▼
 Algorithm Engine               Redis Store
        │
        ▼
 Decision Engine
        │
        ▼
 Allow / Reject Request
```

---

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java 21 |
| Framework | Spring Boot |
| Cache | Redis |
| Database | PostgreSQL |
| Build Tool | Maven |
| Containerization | Docker |
| API | REST |

---

## Project Status

Current Progress

- ✅ Requirement Analysis
- ✅ High-Level Design
- ✅ Database Design
- ✅ ER Diagram
- ⏳ Spring Boot Setup
- ⏳ Token Bucket Implementation
- ⏳ Redis Integration
- ⏳ REST APIs
- ⏳ Docker Support
- ⏳ Monitoring

---

## Planned Project Structure

```
RateShield/
│
├── src/
│   ├── main/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── algorithm/
│   │   ├── config/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── dto/
│   │   └── util/
│   │
│   └── test/
│
├── docs/
├── docker/
├── pom.xml
└── README.md
```

---

## Development Roadmap

### Phase 1
- Project setup
- Spring Boot configuration
- Token Bucket algorithm

### Phase 2
- Redis integration
- Distributed rate limiting

### Phase 3
- Additional rate limiting algorithms
- Configurable rate limiting policies

### Phase 4
- Metrics and monitoring
- Docker support
- Production deployment

---

## Goals

- Build a reusable rate limiting microservice.
- Learn distributed system design.
- Understand production-ready backend architecture.
- Implement and compare multiple rate limiting algorithms.
- Design a service that can be integrated with any backend application.

---

## License

This project is licensed under the MIT License.