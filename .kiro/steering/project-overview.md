---
inclusion: always
---

# Week 3 – Database Design (Spring Boot + JPA + MySQL)

## Project Overview
The goal is to understand:
- **JPA (Java Persistence API)** – How Java objects map to database tables
- **Entity Relationships** – How tables relate to each other (OneToMany, ManyToOne, ManyToMany)
- **Transactions** – How to ensure data consistency when multiple operations happen together

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Data JPA (Hibernate under the hood)
- MySQL Database
- Maven for build management

## Project Structure
```
src/main/java/com/example/coursemanager/
├── entity/          # JPA Entities (maps to DB tables)
├── repository/      # Spring Data repositories (DB access layer)
├── service/         # Business logic + Transactions
├── controller/      # REST API endpoints
└── CourseManagerApplication.java  # Entry point
```

## Key Concepts
- An **Entity** is a Java class annotated with `@Entity` that maps to a database table
- A **Repository** provides CRUD operations without writing SQL
- A **Service** contains business logic and manages transactions
- A **Controller** exposes REST endpoints for the frontend to call

## Entities & Relationships
- **Course** (1) → (Many) **Lesson** — A course has many lessons
- **Student** (Many) ↔ (Many) **Course** — Students enroll in multiple courses

## Coding Standards
- Add clear comments explaining JPA annotations
- Keep methods small and focused
- Use meaningful variable/method names
- Document "why" not just "what"
