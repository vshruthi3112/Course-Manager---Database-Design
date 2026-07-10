# 📖 Complete Step-by-Step Guide: Week 3 – Database Design

## Table of Contents
1. [How to Run the Application](#1-how-to-run-the-application)
2. [Testing All Endpoints](#2-testing-all-endpoints)
3. [Verifying the Database](#3-verifying-the-database)
4. [File-by-File Explanation](#4-file-by-file-explanation)

---

## 1. How to Run the Application

### Step 1: Make sure MySQL is running

Open a terminal and check:
```
mysql -u root -p
```
Enter your password (configured as `root1234` in application.properties).

### Step 2: Create the database

Inside the MySQL prompt:
```sql
CREATE DATABASE course_manager_db;
```
Type `exit` to leave MySQL.

### Step 3: Run the application

Using the Maven wrapper included in the project:
```powershell
./mvnw.ps1 spring-boot:run
```

Or if you have Maven installed:
```powershell
mvn spring-boot:run
```

### What you'll see on success:
```
Adding sample data...
Sample data added!

Started CourseManagerApplication in X.XX seconds
```

The server is now running at: **http://localhost:8080**

---

## 2. Testing All Endpoints

Open a SECOND terminal (keep the server running in the first one).

### 📚 COURSE Endpoints

#### Get all courses
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/courses" -Method GET | ConvertTo-Json -Depth 5
```

#### Get a single course by ID
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/courses/1" -Method GET | ConvertTo-Json -Depth 5
```

#### Search courses by keyword
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/courses/search?keyword=java" -Method GET | ConvertTo-Json -Depth 5
```

#### Create a new course
```powershell
$body = '{"title": "Docker for Beginners", "description": "Learn containerization with Docker"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/courses" -Method POST -Body $body -ContentType "application/json" | ConvertTo-Json
```

#### Update a course
```powershell
$body = '{"title": "Java Mastery", "description": "Updated description for advanced learners"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/courses/1" -Method PUT -Body $body -ContentType "application/json" | ConvertTo-Json
```

#### Delete a course (also deletes its lessons - CASCADE!)
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/courses/3" -Method DELETE
```

#### Get lessons for a course (ordered by lessonOrder)
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/courses/1/lessons" -Method GET | ConvertTo-Json -Depth 3
```

#### Replace all lessons for a course
```powershell
$body = @'
[
  {"title": "Variables", "content": "Learn about types and variables", "lessonOrder": 1},
  {"title": "Loops", "content": "for, while, do-while loops", "lessonOrder": 2},
  {"title": "Functions", "content": "Methods and parameters", "lessonOrder": 3}
]
'@
Invoke-RestMethod -Uri "http://localhost:8080/api/courses/1/lessons" -Method PUT -Body $body -ContentType "application/json" | ConvertTo-Json -Depth 5
```

#### Enroll a student in a course
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/courses/2/enroll/1" -Method POST
```

---

### 👨‍🎓 STUDENT Endpoints

#### Get all students
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/students" -Method GET | ConvertTo-Json -Depth 3
```

#### Get a student by ID
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/students/1" -Method GET | ConvertTo-Json
```

#### Create a new student
```powershell
$body = '{"name": "Charlie", "email": "charlie@example.com"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/students" -Method POST -Body $body -ContentType "application/json" | ConvertTo-Json
```

#### Update a student
```powershell
$body = '{"name": "Alice Smith", "email": "alice.smith@example.com"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/students/1" -Method PUT -Body $body -ContentType "application/json" | ConvertTo-Json
```

#### Delete a student
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/students/3" -Method DELETE
```

---

## 3. Verifying the Database

After running the app and testing endpoints, check your MySQL database:

```sql
-- Connect to MySQL
mysql -u root -p

-- Switch to our database
USE course_manager_db;

-- See all tables JPA created automatically!
SHOW TABLES;
-- Expected: courses, lessons, students, course_students

-- Check courses table structure
DESCRIBE courses;

-- See all courses
SELECT * FROM courses;

-- See all lessons with their course_id (Foreign Key!)
SELECT * FROM lessons;

-- See the JOIN TABLE for ManyToMany
SELECT * FROM course_students;

-- See which students are in which courses (JOIN query)
SELECT c.title AS course, s.name AS student
FROM course_students cs
JOIN courses c ON cs.course_id = c.id
JOIN students s ON cs.student_id = s.id;
```

---

## 4. File-by-File Explanation

### Legend for annotations:
| Symbol | Meaning |
|--------|---------|
| 📦 | Configuration/Setup file |
| 🗄️ | Entity (Database Table) |
| 🔍 | Repository (Data Access) |
| ⚙️ | Service (Business Logic) |
| 🌐 | Controller (REST API) |
| ⚡ | Config/Utility |

---

### 📦 File 1: `pom.xml` (Project Configuration)

**What it is:** The "package.json" of Java projects. Declares all dependencies.

**Key sections:**
```xml
<parent>spring-boot-starter-parent</parent>
```
↑ Inherits Spring Boot's default configurations (you don't need to specify dependency versions individually).

```xml
<java.version>21</java.version>
```
↑ This project uses Java 21.

```xml
<dependency>spring-boot-starter-data-jpa</dependency>
```
↑ THE key dependency! Gives us:
- Hibernate (JPA implementation that converts Java ↔ SQL)
- Spring Data JPA (auto-generated repositories)
- Transaction management

```xml
<dependency>mysql-connector-j</dependency>
```
↑ JDBC driver. Like a "translator" between Java and MySQL.

```xml
<dependency>spring-boot-starter-validation</dependency>
```
↑ Provides validation annotations like `@NotBlank`, `@Email`, `@Size` (available but not yet used in entities).

**Frontend analogy:**
- `pom.xml` = `package.json`
- `<dependencies>` = `"dependencies": {...}`
- `mvn compile` = `npm install && npm run build`

---

### 📦 File 2: `application.properties` (Configuration)

**What it is:** Your `.env` file. Database URL, credentials, and JPA settings.

**Critical settings explained:**

| Property | What it does | Frontend equivalent |
|----------|-------------|-------------------|
| `spring.datasource.url` | MySQL connection string | `DATABASE_URL` in .env |
| `spring.jpa.hibernate.ddl-auto=update` | Auto-creates/updates tables from entities | Like auto-running migrations |
| `spring.jpa.show-sql=true` | Prints every SQL query to console | Console.log for DB queries |
| `spring.jpa.properties.hibernate.format_sql=true` | Formats SQL output for readability | Pretty-print logging |
| `server.port=8080` | HTTP server port | Like Express `app.listen(8080)` |

**ddl-auto options (IMPORTANT to understand):**
- `create` → Drops ALL tables, recreates. YOU LOSE ALL DATA every restart.
- `create-drop` → Same as create, but also drops on shutdown.
- `update` → Only adds new things. Never removes. SAFE for development.
- `validate` → Just checks if schema matches. Used in production.
- `none` → Do nothing. You manage schema manually.

---

### 📦 File 3: `CourseManagerApplication.java` (Entry Point)

**What it is:** The `index.js` / `main()` of your app. Boots everything.

**Line-by-line:**
```java
@SpringBootApplication  // ← Combines 3 annotations in one
```
This single annotation does:
1. `@Configuration` → "This class can configure beans" (objects Spring manages)
2. `@EnableAutoConfiguration` → "Auto-configure everything based on my dependencies"
3. `@ComponentScan` → "Scan all packages for @Controller, @Service, @Repository, @Component"

```java
SpringApplication.run(CourseManagerApplication.class, args);
```
This one line:
1. Creates a Spring application context (the "container" for all objects)
2. Starts embedded Tomcat web server
3. Connects to MySQL
4. Creates/updates DB tables from entities
5. Instantiates all beans (controllers, services, repos)
6. Runs DataInitializer (seeds data)
7. Starts accepting HTTP requests

---

### 🗄️ File 4: `Course.java` (Entity)

**What it is:** A Java class that represents the `courses` DATABASE TABLE.

**The BIG concept:** Every `@Entity` class = 1 database table. Every field = 1 column.

**Line-by-line key annotations:**

```java
@Entity                           // "Hey JPA, this maps to a DB table!"
@Table(name = "courses")          // "The table is called 'courses'"
```

```java
@Id                                         // "This is the primary key"
@GeneratedValue(strategy = IDENTITY)        // "MySQL auto-increments it"
private Long id;
```
↑ Every table needs a unique identifier. `IDENTITY` means MySQL handles numbering (1, 2, 3...).

```java
private String title;
private String description;
```
↑ Simple fields. JPA automatically maps them to columns named `title` and `description`.

**RELATIONSHIPS (the core learning!):**

```java
@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Lesson> lessons = new ArrayList<>();
```
| Part | Meaning |
|------|---------|
| `@OneToMany` | "One Course has Many Lessons" |
| `mappedBy = "course"` | "The Lesson entity's `course` field owns this relationship" |
| `cascade = ALL` | "Save/delete courses → also save/delete their lessons" |
| `orphanRemoval = true` | "If a lesson is removed from this list, delete it from the DB" |

```java
@ManyToMany
@JoinTable(
    name = "course_students",
    joinColumns = @JoinColumn(name = "course_id"),
    inverseJoinColumns = @JoinColumn(name = "student_id")
)
private Set<Student> enrolledStudents = new HashSet<>();
```
| Part | Meaning |
|------|---------|
| `@ManyToMany` | "Many Courses ↔ Many Students" |
| `@JoinTable` | "Create a bridge table called course_students" |
| `joinColumns` | "FK pointing to THIS entity (Course)" |
| `inverseJoinColumns` | "FK pointing to the OTHER entity (Student)" |

**The join table looks like:**
```
course_students
+-----------+------------+
| course_id | student_id |
+-----------+------------+
| 1         | 1          |  ← Alice in Java Basics
| 1         | 2          |  ← Bob in Java Basics
| 2         | 2          |  ← Bob in Web Development
+-----------+------------+
```

**Helper methods (`addLesson`, `enrollStudent`):**
```java
public void addLesson(Lesson lesson) {
    lessons.add(lesson);       // Add to Course's list
    lesson.setCourse(this);    // Set Lesson's FK reference
}
```
WHY? Bidirectional relationships need BOTH sides updated. If you only do `lessons.add(lesson)`, the lesson's `course` field is still null, and the FK won't be set in the DB!

---

### 🗄️ File 5: `Lesson.java` (Entity)

**What it is:** The `lessons` table. The "child" in the Course-Lesson relationship.

**Fields:**
- `id` — Primary key (auto-generated)
- `title` — Lesson title
- `content` — Lesson body content
- `lessonOrder` — Integer for sorting lessons within a course

**KEY CONCEPT: @ManyToOne (the "owning side")**

```java
@ManyToOne
@JoinColumn(name = "course_id")
@JsonIgnore
private Course course;
```

| Part | Meaning |
|------|---------|
| `@ManyToOne` | "Many Lessons belong to One Course" |
| `@JoinColumn(name = "course_id")` | "This table has a course_id FK column" |
| `@JsonIgnore` | "Don't include parent Course when converting to JSON (prevents infinite loop)" |

**WHY is Lesson the "owning" side?**
- The `lessons` table has the `course_id` column (the FK)
- The `courses` table does NOT have any lesson reference
- The entity with the FK column "owns" the relationship
- `mappedBy` in Course says: "Lesson owns it, I'm just the other side"

**The circular reference problem (@JsonIgnore):**
Without `@JsonIgnore`:
```
Course JSON → has lessons → each lesson has course → course has lessons → ... INFINITE!
```
With `@JsonIgnore` on `lesson.course`:
```
Course JSON → has lessons → lessons DON'T include course field → ✅ Clean JSON
```

---

### 🗄️ File 6: `Student.java` (Entity)

**What it is:** The `students` table. Inverse side of the ManyToMany.

**Fields:**
- `id` — Primary key (auto-generated)
- `name` — Student's name
- `email` — Student's email address

```java
@ManyToMany(mappedBy = "enrolledStudents")
@JsonIgnore
private Set<Course> courses = new HashSet<>();
```
↑ `mappedBy = "enrolledStudents"` means: "I'm NOT the owner. The Course entity's `enrolledStudents` field manages the join table."

`@JsonIgnore` prevents infinite loop: Student → Courses → Students → Courses → ...

---

### 🔍 File 7: `CourseRepository.java` (Repository)

**What it is:** Data access interface. Spring auto-generates the implementation!

**The MAGIC: You write the method name, Spring writes the SQL!**

```java
public interface CourseRepository extends JpaRepository<Course, Long> {
```
↑ By extending `JpaRepository`, you get 15+ methods FREE:
- `save()`, `findById()`, `findAll()`, `deleteById()`, `count()`, `existsById()`...

**Derived query (method name → SQL):**
```java
List<Course> findByTitleContainingIgnoreCase(String keyword);
```
Spring reads this as:
```
findBy → SELECT ... WHERE
Title → title column
Containing → LIKE '%...%'
IgnoreCase → LOWER()
```
Generated SQL: `SELECT * FROM courses WHERE LOWER(title) LIKE LOWER('%keyword%')`

---

### 🔍 File 8: `LessonRepository.java`

```java
List<Lesson> findByCourse_IdOrderByLessonOrderAsc(Long courseId);
```
Method name breakdown:
- `findBy` → SELECT WHERE
- `Course_Id` → navigate to the `course` relationship, then its `id` field
- `OrderBy` → ORDER BY
- `LessonOrder` → the `lessonOrder` field
- `Asc` → ascending (1, 2, 3...)

---

### 🔍 File 9: `StudentRepository.java`

```java
Optional<Student> findByEmail(String email);
boolean existsByEmail(String email);
```
- `Optional<Student>` → "might return a student, might return empty" (null-safe)
- `existsByEmail` → returns true/false without loading the full entity (efficient!)

---

### ⚙️ File 10: `CourseService.java` (THE key file for @Transactional!)

**What it is:** Business logic layer. This is where TRANSACTIONS live.

**Constructor Injection (Dependency Injection):**
```java
private final CourseRepository courseRepository;
private final StudentRepository studentRepository;
private final LessonRepository lessonRepository;

public CourseService(CourseRepository courseRepository,
                     StudentRepository studentRepository,
                     LessonRepository lessonRepository) {
    this.courseRepository = courseRepository;
    this.studentRepository = studentRepository;
    this.lessonRepository = lessonRepository;
}
```
Spring sees this constructor, finds matching beans, and passes them in. You never write `new CourseRepository()` yourself!

**@Transactional explained:**

```java
@Transactional
public Course createCourseWithLessons(String title, String description, List<Lesson> lessons) {
    Course course = new Course(title, description);
    for (Lesson lesson : lessons) {
        course.addLesson(lesson);
    }
    return courseRepository.save(course);
}
```

**What @Transactional does here:**
```
BEGIN TRANSACTION
  → Create course object
  → Link all lessons to the course
  → Save course + lessons (cascade saves both)
COMMIT  ← All saved! ✅

--- OR if something fails: ---

BEGIN TRANSACTION
  → Create course object
  → Link all lessons to the course
  → Save → ERROR! 💥
ROLLBACK  ← Nothing is saved! Database unchanged. ✅
```

Without `@Transactional`, you could end up with INCOMPLETE data if a failure happens mid-save.

**Enrollment (also transactional):**
```java
@Transactional
public String enrollStudent(Long courseId, Long studentId) {
    // Looks up both course and student
    // If found, links them via the ManyToMany relationship
    // Returns a status message string
}
```

**Update lessons (transactional for data consistency):**
```java
@Transactional
public Optional<Course> updateCourseLessons(Long courseId, List<Lesson> newLessons) {
    // Clears old lessons (orphanRemoval deletes them from DB)
    // Adds new lessons and links them to the course
    // Saves everything in one atomic operation
}
```

---

### ⚙️ File 11: `StudentService.java`

Standard CRUD service with:
- `getAllStudents()` — returns all students
- `getStudentById(id)` — returns Optional (might be empty)
- `createStudent(student)` — saves a new student
- `updateStudent(id, updatedStudent)` — updates name and email
- `deleteStudent(id)` — removes a student

---

### 🌐 File 12: `CourseController.java` (REST API)

**What it is:** HTTP request handler. Like Express route handlers.

**Base path:** `/api/courses`

**Key annotations mapping:**

| Spring Annotation | Express.js Equivalent |
|-------------------|----------------------|
| `@RestController` | `const router = express.Router()` |
| `@GetMapping` | `router.get(...)` |
| `@PostMapping` | `router.post(...)` |
| `@PutMapping` | `router.put(...)` |
| `@DeleteMapping` | `router.delete(...)` |
| `@PathVariable` | `req.params.id` |
| `@RequestParam` | `req.query.keyword` |
| `@RequestBody` | `req.body` |
| `ResponseEntity.ok(data)` | `res.status(200).json(data)` |
| `ResponseEntity.notFound()` | `res.status(404).send()` |

**Available endpoints:**

| Method | URL | What it does |
|--------|-----|-------------|
| GET | `/api/courses` | List all courses |
| GET | `/api/courses/{id}` | Get one course |
| POST | `/api/courses` | Create a course |
| PUT | `/api/courses/{id}` | Update a course |
| DELETE | `/api/courses/{id}` | Delete a course (cascades to lessons) |
| GET | `/api/courses/search?keyword=x` | Search courses by title |
| POST | `/api/courses/{courseId}/enroll/{studentId}` | Enroll a student |
| GET | `/api/courses/{courseId}/lessons` | Get lessons for a course |
| PUT | `/api/courses/{courseId}/lessons` | Replace all lessons for a course |

**Example flow (GET /api/courses/1):**
```
Browser/Postman → HTTP GET /api/courses/1
  → Spring routes to getCourseById()
    → @PathVariable extracts "1" from URL
    → Calls courseService.getCourseById(1)
      → Repository executes: SELECT * FROM courses WHERE id = 1
      → Returns Optional<Course>
    → If found: 200 OK + Course as JSON
    → If not found: 404 Not Found
```

---

### 🌐 File 13: `StudentController.java`

**Base path:** `/api/students`

| Method | URL | What it does |
|--------|-----|-------------|
| GET | `/api/students` | List all students |
| GET | `/api/students/{id}` | Get one student |
| POST | `/api/students` | Create a student |
| PUT | `/api/students/{id}` | Update a student |
| DELETE | `/api/students/{id}` | Delete a student |

Same pattern as CourseController — thin layer that delegates everything to StudentService.

---

### ⚡ File 14: `DataInitializer.java`

**What it is:** Runs on startup, seeds sample data.

```java
@Component                    // Spring manages this class
implements CommandLineRunner   // run() is called after app starts
```

The `@Transactional` on `run()` means: if inserting ANY seed data fails, ALL inserts are rolled back. You won't end up with half-seeded data.

The `if (courseRepository.count() > 0)` check prevents duplicate seeding on restart.

**What gets seeded:**
- 2 students: Alice (`alice@example.com`) and Bob (`bob@example.com`)
- Course 1: "Java Basics" with 3 lessons (Variables, Loops, Classes) — Alice and Bob enrolled
- Course 2: "Web Development" with 2 lessons (HTML, CSS) — Bob enrolled

---

### ⚡ File 15: `GlobalExceptionHandler.java`

**What it is:** Global error handler (like Express error middleware).

```java
@RestControllerAdvice    // "Apply to ALL controllers"
```

```java
@ExceptionHandler(Exception.class)  // Catches ALL exceptions
public ResponseEntity<String> handleError(Exception e) {
    return ResponseEntity.badRequest().body("Error: " + e.getMessage());
}
```
↑ Catches any unhandled exception thrown by controllers/services and returns a 400 response with the error message, instead of an ugly 500 stack trace.

---

## 🧠 Concept Summary

| Concept | One-line explanation |
|---------|---------------------|
| **JPA** | Maps Java classes to database tables using annotations |
| **Entity** | A Java class = A database table |
| **@Id + @GeneratedValue** | Auto-incrementing primary key |
| **@OneToMany** | "I have many children" (Course → Lessons) |
| **@ManyToOne** | "I belong to a parent" (Lesson → Course) |
| **@ManyToMany** | "We reference each other via a join table" (Course ↔ Student) |
| **@JoinTable** | Creates the bridge table for ManyToMany |
| **@JoinColumn** | Specifies the FK column name |
| **mappedBy** | "I'm the non-owning side, the other entity manages the FK" |
| **cascade** | Operations propagate to related entities |
| **orphanRemoval** | Delete children that are removed from the parent's collection |
| **Repository** | Interface → Spring auto-generates SQL CRUD methods |
| **@Transactional** | All-or-nothing: if any operation fails, roll back everything |
| **@Service** | Business logic lives here |
| **@RestController** | Handles HTTP requests, returns JSON |
| **@JsonIgnore** | Prevents infinite loops in JSON serialization |

---

## 🏗️ Architecture Flow

```
HTTP Request
    │
    ▼
┌──────────────┐
│  Controller  │  ← Parses request, delegates to service
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   Service    │  ← Business logic, @Transactional boundaries
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  Repository  │  ← Auto-generated SQL queries
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   Database   │  ← MySQL stores the actual data
└──────────────┘
```

Each layer only talks to the layer directly below it. This is called **Separation of Concerns** – the same idea as keeping components, services, and API calls separate in frontend frameworks.
