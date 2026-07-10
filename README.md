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


---

## Week 4 – Testing: JUnit, Mockito & Service Layer Testing

### What You'll Learn

| Concept | What It Is | Analogy |
|---------|-----------|---------|
| **JUnit** | Testing framework | The test lab – provides `@Test`, assertions, test lifecycle |
| **Mockito** | Mocking library | Fake ingredients for a chef – creates fake dependencies |
| **Service Layer Testing** | Testing strategy | Testing the chef's recipe without needing a real farm |
| **MockMvc** | HTTP test client | A fake browser that sends requests without starting the server |
| **JaCoCo** | Coverage tool | A highlighter that shows which lines your tests actually ran |

### JUnit vs Mockito vs Service Layer Testing

#### JUnit – The Foundation

JUnit is the **framework** that lets you write and run tests in Java. Think of it as the test lab with equipment.

```java
@Test  // "this method is a test"
void shouldAddNumbers() {
    int result = 2 + 3;
    assertEquals(5, result);  // check: is the answer 5?
}
```

Key annotations:
- `@Test` – marks a method as a test case
- `@BeforeEach` – runs setup before each test
- `@DisplayName("...")` – gives a readable name to the test

Key assertions:
- `assertEquals(expected, actual)` – are they equal?
- `assertTrue(condition)` – is it true?
- `assertFalse(condition)` – is it false?
- `assertNotNull(value)` – is it not null?

#### Mockito – The Fake Object Creator

Mockito creates **fake (mock) objects** so you can test one class without its real dependencies.

**Why?** Your `CourseService` depends on `CourseRepository` (which talks to the database). In a unit test, you don't want a real database. Mockito creates a fake repository you control:

```java
@Mock
private CourseRepository courseRepository;  // FAKE repository

// Tell the fake: "when findAll() is called, return this list"
when(courseRepository.findAll()).thenReturn(List.of(course1, course2));

// Verify the fake was actually called
verify(courseRepository, times(1)).findAll();
```

#### Service Layer Testing – The Strategy

This isn't a library – it's a **strategy**: test business logic by mocking the data layer.

```java
@Mock private CourseRepository repo;           // fake DB
@InjectMocks private CourseService service;    // real service + fake DB injected

@Test
void testGetAllCourses() {
    when(repo.findAll()).thenReturn(courses);   // arrange
    List<Course> result = service.getAllCourses(); // act
    assertEquals(2, result.size());            // assert
}
```

### Test Structure

```
src/test/java/com/example/coursemanager/
├── service/                         ← SERVICE LAYER TESTS (JUnit + Mockito)
│   ├── CourseServiceTest.java      (16 tests – all business logic)
│   └── StudentServiceTest.java    (8 tests – CRUD operations)
│
└── controller/                      ← CONTROLLER TESTS (MockMvc + Mockito)
    ├── CourseControllerTest.java   (12 tests – HTTP endpoints)
    └── StudentControllerTest.java (8 tests – HTTP endpoints)
```

**Total: 44 unit tests**

### Test Coverage Results

| Package | Coverage | Notes |
|---------|----------|-------|
| **service** | 100% | All business logic tested |
| **controller** | 100% | All HTTP endpoints tested |
| **entity** | 96% | Covered via service/controller tests |
| **config** | 2% | Infrastructure code (not business logic) |
| **Overall** | **78%** | Exceeds the 70% goal ✓ |

### How to Run Tests

```bash
# Run all tests + generate coverage report
./mvnw.ps1 test

# Open the coverage report in a browser:
# target/site/jacoco/index.html
```

The HTML report shows:
- 🟢 Green = code that was executed during tests
- 🔴 Red = code that was NOT executed
- Percentages for each class, package, and overall

### Key Testing Patterns

#### Pattern 1: Service Test (testing business logic)

```java
// 1. Create fakes
@Mock private SomeRepository repo;
@InjectMocks private SomeService service;

// 2. Tell the fake what to return
when(repo.findById(1L)).thenReturn(Optional.of(entity));

// 3. Call the real method
Optional<Entity> result = service.getById(1L);

// 4. Check the result
assertTrue(result.isPresent());
assertEquals("expected", result.get().getName());

// 5. Verify the fake was called
verify(repo, times(1)).findById(1L);
```

#### Pattern 2: Controller Test (testing HTTP endpoints)

```java
@WebMvcTest(SomeController.class)  // only load web layer
class SomeControllerTest {

    @Autowired private MockMvc mockMvc;       // fake HTTP client
    @MockBean private SomeService service;    // fake service

    @Test
    void testGetEndpoint() throws Exception {
        when(service.getAll()).thenReturn(data);

        mockMvc.perform(get("/api/things"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("expected"));
    }
}
```

### Common Annotations Cheat Sheet

| Annotation | Library | What it does |
|-----------|---------|-------------|
| `@Test` | JUnit | Marks a method as a test |
| `@BeforeEach` | JUnit | Runs before each test (setup) |
| `@DisplayName` | JUnit | Human-readable test name |
| `@Mock` | Mockito | Creates a fake object |
| `@InjectMocks` | Mockito | Creates real object with fakes injected |
| `@WebMvcTest` | Spring | Loads only the web layer for testing |
| `@MockBean` | Spring | Puts a fake into Spring's context |

### What `L` Means in `1L`, `2L`, `99L`

Entity IDs are `Long` (64-bit). By default, `2` is an `int` (32-bit). Adding `L` tells Java: "treat this number as a Long."

```java
Long id = 2L;  // ✓ correct: Long value
Long id = 2;   // ✗ type mismatch: int vs Long
```
