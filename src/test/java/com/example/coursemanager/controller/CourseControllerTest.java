package com.example.coursemanager.controller;

// ============================================================
// CourseControllerTest.java - Unit Tests for CourseController
// ============================================================
//
// CONTROLLER TESTS ARE DIFFERENT from service tests!
//
// With service tests, we tested Java methods directly.
// With controller tests, we simulate HTTP requests (GET, POST, PUT, DELETE)
// and check the HTTP response (status code, JSON body).
//
// KEY TOOL: MockMvc
// MockMvc = a fake web server that lets us send HTTP requests in tests
// without starting the actual application.
//
// ANNOTATIONS EXPLAINED:
// @WebMvcTest = "only load the web layer (controller), not the whole app"
//   This is faster because it doesn't start the database, services, etc.
// @MockBean = like @Mock, but specifically for Spring's application context
//   It puts a fake service into Spring's "container"
// ============================================================

import com.example.coursemanager.entity.Course;
import com.example.coursemanager.entity.Lesson;
import com.example.coursemanager.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest = only load CourseController (not the whole application)
// This means Spring will NOT connect to a database or start other services
@WebMvcTest(CourseController.class)
class CourseControllerTest {

    // MockMvc = fake HTTP client for testing controllers
    // It simulates sending HTTP requests and reading responses
    @Autowired
    private MockMvc mockMvc;

    // @MockBean = create a fake CourseService and put it in Spring's context
    // The controller will use this fake service instead of the real one
    @MockBean
    private CourseService courseService;

    // ObjectMapper converts Java objects ↔ JSON strings
    // We need this to send JSON in POST/PUT requests
    @Autowired
    private ObjectMapper objectMapper;

    // ============================================================
    // TEST: GET /api/courses - Get all courses
    // ============================================================

    @Test
    @DisplayName("GET /api/courses should return list of courses with status 200")
    void getAllCourses_ShouldReturnListOfCourses() throws Exception {
        // ARRANGE: Create test data
        Course course1 = new Course("Java Basics", "Learn Java");
        course1.setId(1L);
        Course course2 = new Course("Spring Boot", "Build apps");
        course2.setId(2L);

        // Tell the fake service what to return
        when(courseService.getAllCourses()).thenReturn(Arrays.asList(course1, course2));

        // ACT & ASSERT: Send GET request and check response
        mockMvc.perform(get("/api/courses"))          // Send GET /api/courses
            .andExpect(status().isOk())               // Expect HTTP 200
            .andExpect(jsonPath("$.length()").value(2))  // Expect 2 items in JSON array
            .andExpect(jsonPath("$[0].title").value("Java Basics"))  // Check first item
            .andExpect(jsonPath("$[1].title").value("Spring Boot")); // Check second item

        verify(courseService, times(1)).getAllCourses();
    }

    // ============================================================
    // TEST: GET /api/courses/{id} - Get course by ID
    // ============================================================

    @Test
    @DisplayName("GET /api/courses/1 should return course with status 200")
    void getCourseById_ShouldReturnCourse_WhenExists() throws Exception {
        // ARRANGE
        Course course = new Course("Java Basics", "Learn Java");
        course.setId(1L);
        when(courseService.getCourseById(1L)).thenReturn(Optional.of(course));

        // ACT & ASSERT
        mockMvc.perform(get("/api/courses/1"))
            .andExpect(status().isOk())                        // HTTP 200
            .andExpect(jsonPath("$.title").value("Java Basics"))
            .andExpect(jsonPath("$.description").value("Learn Java"));
    }

    @Test
    @DisplayName("GET /api/courses/99 should return 404 when not found")
    void getCourseById_ShouldReturn404_WhenNotExists() throws Exception {
        // ARRANGE: service returns empty (not found)
        when(courseService.getCourseById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        mockMvc.perform(get("/api/courses/99"))
            .andExpect(status().isNotFound());  // HTTP 404
    }

    // ============================================================
    // TEST: POST /api/courses - Create a new course
    // ============================================================

    @Test
    @DisplayName("POST /api/courses should create course and return it")
    void createCourse_ShouldReturnCreatedCourse() throws Exception {
        // ARRANGE
        Course course = new Course("New Course", "New Description");
        Course savedCourse = new Course("New Course", "New Description");
        savedCourse.setId(1L);

        when(courseService.createCourse(any(Course.class))).thenReturn(savedCourse);

        // ACT & ASSERT: Send POST with JSON body
        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)  // Tell server we're sending JSON
                .content(objectMapper.writeValueAsString(course)))  // Convert course to JSON
            .andExpect(status().isOk())                   // HTTP 200
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("New Course"))
            .andExpect(jsonPath("$.description").value("New Description"));
    }

    // ============================================================
    // TEST: PUT /api/courses/{id} - Update a course
    // ============================================================

    @Test
    @DisplayName("PUT /api/courses/1 should update and return course")
    void updateCourse_ShouldReturnUpdatedCourse_WhenExists() throws Exception {
        // ARRANGE
        Course updatedCourse = new Course("Updated Title", "Updated Desc");
        updatedCourse.setId(1L);
        
        when(courseService.updateCourse(eq(1L), any(Course.class)))
            .thenReturn(Optional.of(updatedCourse));

        // ACT & ASSERT
        mockMvc.perform(put("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCourse)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @DisplayName("PUT /api/courses/99 should return 404 when course not found")
    void updateCourse_ShouldReturn404_WhenNotExists() throws Exception {
        // ARRANGE
        Course updatedCourse = new Course("Updated", "Desc");
        when(courseService.updateCourse(eq(99L), any(Course.class)))
            .thenReturn(Optional.empty());

        // ACT & ASSERT
        mockMvc.perform(put("/api/courses/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCourse)))
            .andExpect(status().isNotFound());
    }

    // ============================================================
    // TEST: DELETE /api/courses/{id} - Delete a course
    // ============================================================

    @Test
    @DisplayName("DELETE /api/courses/1 should delete course successfully")
    void deleteCourse_ShouldReturn200() throws Exception {
        // ARRANGE
        doNothing().when(courseService).deleteCourse(1L);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/courses/1"))
            .andExpect(status().isOk());

        verify(courseService, times(1)).deleteCourse(1L);
    }

    // ============================================================
    // TEST: GET /api/courses/search?keyword=java
    // ============================================================

    @Test
    @DisplayName("GET /api/courses/search?keyword=java should return matching courses")
    void searchCourses_ShouldReturnMatchingCourses() throws Exception {
        // ARRANGE
        Course course = new Course("Java Basics", "Learn Java");
        course.setId(1L);
        when(courseService.searchCourses("java")).thenReturn(List.of(course));

        // ACT & ASSERT
        mockMvc.perform(get("/api/courses/search").param("keyword", "java"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Java Basics"));
    }

    // ============================================================
    // TEST: POST /api/courses/{courseId}/enroll/{studentId}
    // ============================================================

    @Test
    @DisplayName("POST /api/courses/1/enroll/1 should enroll student")
    void enrollStudent_ShouldReturnSuccessMessage() throws Exception {
        // ARRANGE
        when(courseService.enrollStudent(1L, 1L)).thenReturn("Student enrolled successfully!");

        // ACT & ASSERT
        mockMvc.perform(post("/api/courses/1/enroll/1"))
            .andExpect(status().isOk())
            .andExpect(content().string("Student enrolled successfully!"));
    }

    // ============================================================
    // TEST: GET /api/courses/{courseId}/lessons
    // ============================================================

    @Test
    @DisplayName("GET /api/courses/1/lessons should return lessons")
    void getCourseLessons_ShouldReturnLessons() throws Exception {
        // ARRANGE
        Lesson lesson1 = new Lesson("Intro", "Welcome!", 1);
        lesson1.setId(1L);
        Lesson lesson2 = new Lesson("Variables", "Learn vars", 2);
        lesson2.setId(2L);
        
        when(courseService.getLessonsForCourse(1L))
            .thenReturn(Arrays.asList(lesson1, lesson2));

        // ACT & ASSERT
        mockMvc.perform(get("/api/courses/1/lessons"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Intro"))
            .andExpect(jsonPath("$[1].title").value("Variables"));
    }

    // ============================================================
    // TEST: PUT /api/courses/{courseId}/lessons
    // ============================================================

    @Test
    @DisplayName("PUT /api/courses/1/lessons should update lessons")
    void updateCourseLessons_ShouldReturnUpdatedCourse() throws Exception {
        // ARRANGE
        Lesson lesson = new Lesson("New Lesson", "Content", 1);
        Course courseWithLessons = new Course("Java", "Learn Java");
        courseWithLessons.setId(1L);
        courseWithLessons.addLesson(lesson);

        when(courseService.updateCourseLessons(eq(1L), anyList()))
            .thenReturn(Optional.of(courseWithLessons));

        // ACT & ASSERT
        mockMvc.perform(put("/api/courses/1/lessons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(lesson))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Java"));
    }

    @Test
    @DisplayName("PUT /api/courses/99/lessons should return 404 when course not found")
    void updateCourseLessons_ShouldReturn404_WhenCourseNotFound() throws Exception {
        // ARRANGE
        when(courseService.updateCourseLessons(eq(99L), anyList()))
            .thenReturn(Optional.empty());

        // ACT & ASSERT
        mockMvc.perform(put("/api/courses/99/lessons")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
            .andExpect(status().isNotFound());
    }
}
