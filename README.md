# Week 3 – Database Design: JPA, Entity Relationships & Transactions

A Spring Boot learning project for frontend developers transitioning to full-stack.

## What You'll Learn

| Concept | What It Means | Frontend Equivalent |
|---------|---------------|-------------------|
| **JPA Entity** | A Java class that maps to a DB table | A TypeScript interface/model |
| **Repository** | Auto-generated CRUD methods | An API client/service |
| **@Transactional** | All-or-nothing DB operations | (no direct equivalent – backend magic!) |
| **OneToMany** | One parent has many children | Nested arrays in JSON |
| **ManyToMany** | Two entities reference each other | Join table in the DB |

## Prerequisites

1. **Java 17+** installed (`java -version` to check)
2. **Maven** installed (`mvn -version` to check)
3. **MySQL** installed and running

## Setup

### 1. Create the MySQL Database

```sql
-- Open MySQL CLI or workbench and run:
CREATE DATABASE course_manager_db;
```

### 2. Configure Database Connection

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_ACTUAL_PASSWORD
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080` and automatically:
- Creates database tables from your entity classes
- Seeds sample data (courses, lessons, students)

## Project Structure Explained

```
src/main/java/com/example/coursemanager/
│
├── entity/                    ← DATABASE TABLES as Java classes
│   ├── Course.java           (courses table)
│   ├── Lesson.java           (lessons table, FK to courses)
│   └── Student.java          (students table, join table with courses)
│
├── repository/                ← DATA ACCESS (auto-generated SQL)
│   ├── CourseRepository.java
│   ├── LessonRepository.java
│   └── StudentRepository.java
│
├── service/                   ← BUSINESS LOGIC + TRANSACTIONS
│   ├── CourseService.java
│   └── StudentService.java
│
├── controller/                ← REST API ENDPOINTS
│   ├── CourseController.java
│   └── StudentController.java
│
├── config/                    ← APP CONFIGURATION
│   ├── DataInitializer.java  (seeds sample data)
│   └── GlobalExceptionHandler.java (error handling)
│
└── CourseManagerApplication.java  ← ENTRY POINT
```

## Entity Relationships Diagram

```
┌─────────────┐       ┌─────────────────┐       ┌──────────────┐
│   COURSE    │       │ COURSE_STUDENTS │       │   STUDENT    │
├─────────────┤       │  (join table)   │       ├──────────────┤
│ id (PK)     │──┐    ├─────────────────┤    ┌──│ id (PK)      │
│ title       │  │    │ course_id (FK)──│────┘  │ name         │
│ description │  │    │ student_id (FK)─│───────│ email        │
│ created_at  │  │    └─────────────────┘       └──────────────┘
└─────────────┘  │
                 │    ┌──────────────┐
                 │    │    LESSON    │
                 │    ├──────────────┤
                 └────│ course_id(FK)│
                      │ id (PK)     │
                      │ title       │
                      │ content     │
                      │ lesson_order│
                      └──────────────┘
```

## API Endpoints

### Courses

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/courses` | List all courses |
| GET | `/api/courses/{id}` | Get one course |
| GET | `/api/courses/search?keyword=spring` | Search by title |
| POST | `/api/courses` | Create a course |
| POST | `/api/courses/with-lessons` | Create course + lessons (transactional!) |
| PUT | `/api/courses/{id}` | Update a course |
| DELETE | `/api/courses/{id}` | Delete a course (cascades to lessons) |
| POST | `/api/courses/{id}/enroll/{studentId}` | Enroll student |
| DELETE | `/api/courses/{id}/unenroll/{studentId}` | Unenroll student |
| GET | `/api/courses/{id}/lessons` | Get course lessons |

### Students

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/students` | List all students |
| GET | `/api/students/{id}` | Get one student |
| POST | `/api/students` | Create a student |
| PUT | `/api/students/{id}` | Update a student |
| DELETE | `/api/students/{id}` | Delete a student |

## Test with cURL

```bash
# Get all courses
curl http://localhost:8080/api/courses

# Create a new student
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"name": "Diana Prince", "email": "diana@example.com"}'

# Create a course with lessons (TRANSACTIONAL!)
curl -X POST http://localhost:8080/api/courses/with-lessons \
  -H "Content-Type: application/json" \
  -d '{
    "course": {
      "title": "Database Design 101",
      "description": "Learn SQL and JPA"
    },
    "lessons": [
      {"title": "What is a Database?", "content": "A database stores data...", "lessonOrder": 1},
      {"title": "Tables and Rows", "content": "Tables organize data...", "lessonOrder": 2}
    ]
  }'

# Enroll student 1 in course 1
curl -X POST http://localhost:8080/api/courses/1/enroll/1

# Search courses
curl "http://localhost:8080/api/courses/search?keyword=spring"
```

## Key Concepts to Understand

### 1. JPA Entity = Database Table
Each `@Entity` class becomes a table. Each field becomes a column.
JPA handles the SQL `CREATE TABLE` for you.

### 2. Relationships
- **@OneToMany / @ManyToOne**: Parent-child (Course → Lessons)
- **@ManyToMany**: Both sides can have many (Students ↔ Courses)
- **Join Table**: A hidden table that stores ManyToMany links

### 3. Transactions (@Transactional)
Groups multiple DB operations into one atomic unit.
If any operation fails, ALL are rolled back.
Critical for data consistency!

### 4. Cascade
When you save/delete a parent, the operation "cascades" to children.
Example: Deleting a Course also deletes all its Lessons.

## Kiro Features Used

- **Steering files** (`.kiro/steering/`): Auto-loaded context about coding standards
- **Hooks**: Auto-triggered checks when you edit entity or service files
- **Documentation-first**: Every file has detailed educational comments

## Next Steps

- Try adding a new entity (e.g., `Instructor`)
- Add a new relationship (Course has one Instructor)
- Try breaking a transaction to see rollback in action
- Check the SQL output in the console to understand what JPA generates
