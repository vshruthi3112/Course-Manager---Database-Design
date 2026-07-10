package com.example.coursemanager.service;

// ============================================================
// CourseServiceTest.java - Unit Tests for CourseService
// ============================================================
// 
// WHAT THIS FILE DOES:
// Tests all methods in CourseService WITHOUT connecting to a real database.
// We use Mockito to create fake repositories, then verify the service
// logic works correctly.
//
// KEY CONCEPT:
// Unit Test = test ONE class in isolation
// We're testing CourseService, so we FAKE everything it depends on
// (CourseRepository, StudentRepository, LessonRepository)
// ============================================================

import com.example.coursemanager.entity.Course;
import com.example.coursemanager.entity.Lesson;
import com.example.coursemanager.entity.Student;
import com.example.coursemanager.repository.CourseRepository;
import com.example.coursemanager.repository.LessonRepository;
import com.example.coursemanager.repository.StudentRepository;

// JUnit 5 imports — these come from the JUnit framework
import org.junit.jupiter.api.BeforeEach;       // Runs before EACH test method
import org.junit.jupiter.api.DisplayName;       // Gives a human-readable name to tests
import org.junit.jupiter.api.Test;              // Marks a method as a test

// Mockito imports — for creating fake objects
import org.mockito.InjectMocks;   // Creates the real service and injects fakes into it
import org.mockito.Mock;          // Creates a fake (mock) object
import org.mockito.MockitoAnnotations;  // Activates the @Mock annotations

// Static imports let us write "when(...)" instead of "Mockito.when(...)"
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class CourseServiceTest {

    // ============================================================
    // STEP 1: Declare the mocks (fake objects)
    // ============================================================
    
    // @Mock = "create a FAKE CourseRepository"
    // This fake won't talk to a real database — we control what it returns
    @Mock
    private CourseRepository courseRepository;

    // @Mock = "create a FAKE StudentRepository"
    @Mock
    private StudentRepository studentRepository;

    // @Mock = "create a FAKE LessonRepository"
    @Mock
    private LessonRepository lessonRepository;

    // @InjectMocks = "create a REAL CourseService, but inject the fakes above into it"
    // So CourseService thinks it has real repositories, but they're actually our fakes
    @InjectMocks
    private CourseService courseService;

    // Test data — we'll reuse these in multiple tests
    private Course course1;
    private Course course2;
    private Student student1;

    // ============================================================
    // STEP 2: Setup — runs BEFORE each test
    // ============================================================
    
    @BeforeEach  // JUnit annotation: "run this method before every @Test method"
    void setUp() {
        // This line activates all the @Mock and @InjectMocks annotations above
        MockitoAnnotations.openMocks(this);
        
        // Create test data that we'll use across multiple tests
        course1 = new Course("Java Basics", "Learn Java fundamentals");
        course1.setId(1L);  // Simulate that this course has ID = 1 in the DB
        
        course2 = new Course("Spring Boot", "Build web apps with Spring");
        course2.setId(2L);
        
        student1 = new Student("Alice", "alice@example.com");
        student1.setId(1L);
    }

    // ============================================================
    // STEP 3: The actual tests
    // ============================================================

    // --- Tests for getAllCourses() ---

    @Test  // JUnit annotation: "this method is a test"
    @DisplayName("getAllCourses should return all courses from repository")
    void getAllCourses_ShouldReturnAllCourses() {
        // ARRANGE: Tell the fake repository what to return
        // "when someone calls findAll() on the fake repo, return this list"
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course1, course2));

        // ACT: Call the actual service method we're testing
        List<Course> result = courseService.getAllCourses();

        // ASSERT: Check the result is what we expect
        assertEquals(2, result.size());           // Should have 2 courses
        assertEquals("Java Basics", result.get(0).getTitle());  // First course title
        assertEquals("Spring Boot", result.get(1).getTitle());  // Second course title
        
        // VERIFY: Make sure the service actually called the repository
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllCourses should return empty list when no courses exist")
    void getAllCourses_ShouldReturnEmptyList_WhenNoCourses() {
        // ARRANGE: Repository returns empty list
        when(courseRepository.findAll()).thenReturn(List.of());

        // ACT
        List<Course> result = courseService.getAllCourses();

        // ASSERT
        assertTrue(result.isEmpty());  // The list should be empty
        verify(courseRepository, times(1)).findAll();
    }

    // --- Tests for getCourseById() ---

    @Test
    @DisplayName("getCourseById should return course when it exists")
    void getCourseById_ShouldReturnCourse_WhenExists() {
        // ARRANGE: When someone asks for ID=1, return course1
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));

        // ACT
        Optional<Course> result = courseService.getCourseById(1L);

        // ASSERT
        assertTrue(result.isPresent());           // Should find something
        assertEquals("Java Basics", result.get().getTitle());  // Should be our course
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getCourseById should return empty when course doesn't exist")
    void getCourseById_ShouldReturnEmpty_WhenNotExists() {
        // ARRANGE: When someone asks for ID=99, return empty (not found)
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Optional<Course> result = courseService.getCourseById(99L);

        // ASSERT
        assertFalse(result.isPresent());  // Should NOT find anything
        verify(courseRepository, times(1)).findById(99L);
    }

    // --- Tests for createCourse() ---

    @Test
    @DisplayName("createCourse should save and return the new course")
    void createCourse_ShouldSaveAndReturnCourse() {
        // ARRANGE: When save() is called with any Course, return course1
        when(courseRepository.save(any(Course.class))).thenReturn(course1);

        // ACT
        Course result = courseService.createCourse(course1);

        // ASSERT
        assertNotNull(result);                    // Should not be null
        assertEquals("Java Basics", result.getTitle());
        assertEquals(1L, result.getId());
        
        // verify save() was called exactly once
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    // --- Tests for deleteCourse() ---

    @Test
    @DisplayName("deleteCourse should call repository deleteById")
    void deleteCourse_ShouldCallRepositoryDelete() {
        // ARRANGE: deleteById returns nothing (void), so we just let it happen
        doNothing().when(courseRepository).deleteById(1L);

        // ACT
        courseService.deleteCourse(1L);

        // ASSERT: Verify deleteById was called with the correct ID
        verify(courseRepository, times(1)).deleteById(1L);
    }

    // --- Tests for updateCourse() ---

    @Test
    @DisplayName("updateCourse should update and return course when it exists")
    void updateCourse_ShouldUpdateCourse_WhenExists() {
        // ARRANGE
        Course updatedData = new Course("Updated Title", "Updated Description");
        Course savedCourse = new Course("Updated Title", "Updated Description");
        savedCourse.setId(1L);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));
        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // ACT
        Optional<Course> result = courseService.updateCourse(1L, updatedData);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals("Updated Title", result.get().getTitle());
        assertEquals("Updated Description", result.get().getDescription());
        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("updateCourse should return empty when course doesn't exist")
    void updateCourse_ShouldReturnEmpty_WhenNotExists() {
        // ARRANGE
        Course updatedData = new Course("Updated Title", "Updated Description");
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Optional<Course> result = courseService.updateCourse(99L, updatedData);

        // ASSERT
        assertFalse(result.isPresent());
        verify(courseRepository, times(1)).findById(99L);
        // save() should NEVER be called if course doesn't exist
        verify(courseRepository, never()).save(any(Course.class));
    }

    // --- Tests for searchCourses() ---

    @Test
    @DisplayName("searchCourses should return matching courses")
    void searchCourses_ShouldReturnMatchingCourses() {
        // ARRANGE
        when(courseRepository.findByTitleContainingIgnoreCase("java"))
            .thenReturn(List.of(course1));

        // ACT
        List<Course> result = courseService.searchCourses("java");

        // ASSERT
        assertEquals(1, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
        verify(courseRepository, times(1)).findByTitleContainingIgnoreCase("java");
    }

    // --- Tests for enrollStudent() ---

    @Test
    @DisplayName("enrollStudent should enroll student successfully")
    void enrollStudent_ShouldEnrollSuccessfully() {
        // ARRANGE
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(courseRepository.save(any(Course.class))).thenReturn(course1);

        // ACT
        String result = courseService.enrollStudent(1L, 1L);

        // ASSERT
        assertEquals("Student enrolled successfully!", result);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("enrollStudent should return error when course not found")
    void enrollStudent_ShouldReturnError_WhenCourseNotFound() {
        // ARRANGE
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        String result = courseService.enrollStudent(99L, 1L);

        // ASSERT
        assertEquals("Course not found", result);
        // save() should never be called
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("enrollStudent should return error when student not found")
    void enrollStudent_ShouldReturnError_WhenStudentNotFound() {
        // ARRANGE
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        String result = courseService.enrollStudent(1L, 99L);

        // ASSERT
        assertEquals("Student not found", result);
        verify(courseRepository, never()).save(any(Course.class));
    }

    // --- Tests for getLessonsForCourse() ---

    @Test
    @DisplayName("getLessonsForCourse should return lessons sorted by order")
    void getLessonsForCourse_ShouldReturnLessons() {
        // ARRANGE
        Lesson lesson1 = new Lesson("Intro", "Welcome!", 1);
        Lesson lesson2 = new Lesson("Variables", "Learn vars", 2);
        when(lessonRepository.findByCourse_IdOrderByLessonOrderAsc(1L))
            .thenReturn(Arrays.asList(lesson1, lesson2));

        // ACT
        List<Lesson> result = courseService.getLessonsForCourse(1L);

        // ASSERT
        assertEquals(2, result.size());
        assertEquals("Intro", result.get(0).getTitle());
        assertEquals("Variables", result.get(1).getTitle());
    }

    // --- Tests for createCourseWithLessons() ---

    @Test
    @DisplayName("createCourseWithLessons should create course with all lessons")
    void createCourseWithLessons_ShouldCreateCourseWithLessons() {
        // ARRANGE
        Lesson lesson1 = new Lesson("Intro", "Welcome!", 1);
        Lesson lesson2 = new Lesson("Basics", "Fundamentals", 2);
        List<Lesson> lessons = Arrays.asList(lesson1, lesson2);

        // When save is called, return a course with lessons attached
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
            Course savedCourse = invocation.getArgument(0);
            savedCourse.setId(1L);
            return savedCourse;
        });

        // ACT
        Course result = courseService.createCourseWithLessons(
            "New Course", "Description", lessons);

        // ASSERT
        assertNotNull(result);
        assertEquals("New Course", result.getTitle());
        assertEquals(2, result.getLessons().size());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    // --- Tests for updateCourseLessons() ---

    @Test
    @DisplayName("updateCourseLessons should replace lessons for existing course")
    void updateCourseLessons_ShouldReplaceLessons_WhenCourseExists() {
        // ARRANGE
        Lesson newLesson = new Lesson("New Lesson", "New content", 1);
        List<Lesson> newLessons = List.of(newLesson);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> 
            invocation.getArgument(0));

        // ACT
        Optional<Course> result = courseService.updateCourseLessons(1L, newLessons);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getLessons().size());
        assertEquals("New Lesson", result.get().getLessons().get(0).getTitle());
    }

    @Test
    @DisplayName("updateCourseLessons should return empty when course not found")
    void updateCourseLessons_ShouldReturnEmpty_WhenCourseNotFound() {
        // ARRANGE
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Optional<Course> result = courseService.updateCourseLessons(99L, List.of());

        // ASSERT
        assertFalse(result.isPresent());
        verify(courseRepository, never()).save(any(Course.class));
    }
}
