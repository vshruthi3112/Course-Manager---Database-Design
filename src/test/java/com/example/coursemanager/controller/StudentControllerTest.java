package com.example.coursemanager.controller;

// ============================================================
// StudentControllerTest.java - Unit Tests for StudentController
// ============================================================
//
// Same pattern as CourseControllerTest:
// - @WebMvcTest loads only the controller layer
// - @MockBean fakes the service
// - MockMvc simulates HTTP requests
// ============================================================

import com.example.coursemanager.entity.Student;
import com.example.coursemanager.service.StudentService;
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

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    // ============================================================
    // TEST: GET /api/students - Get all students
    // ============================================================

    @Test
    @DisplayName("GET /api/students should return all students with status 200")
    void getAllStudents_ShouldReturnAllStudents() throws Exception {
        // ARRANGE
        Student student1 = new Student("Alice", "alice@example.com");
        student1.setId(1L);
        Student student2 = new Student("Bob", "bob@example.com");
        student2.setId(2L);

        when(studentService.getAllStudents()).thenReturn(Arrays.asList(student1, student2));

        // ACT & ASSERT
        mockMvc.perform(get("/api/students"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Alice"))
            .andExpect(jsonPath("$[1].name").value("Bob"));
    }

    @Test
    @DisplayName("GET /api/students should return empty list when no students")
    void getAllStudents_ShouldReturnEmptyList() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of());

        mockMvc.perform(get("/api/students"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    // ============================================================
    // TEST: GET /api/students/{id} - Get student by ID
    // ============================================================

    @Test
    @DisplayName("GET /api/students/1 should return student when exists")
    void getStudentById_ShouldReturnStudent_WhenExists() throws Exception {
        // ARRANGE
        Student student = new Student("Alice", "alice@example.com");
        student.setId(1L);
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));

        // ACT & ASSERT
        mockMvc.perform(get("/api/students/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("GET /api/students/99 should return 404 when not found")
    void getStudentById_ShouldReturn404_WhenNotExists() throws Exception {
        when(studentService.getStudentById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/students/99"))
            .andExpect(status().isNotFound());
    }

    // ============================================================
    // TEST: POST /api/students - Create a new student
    // ============================================================

    @Test
    @DisplayName("POST /api/students should create and return student")
    void createStudent_ShouldReturnCreatedStudent() throws Exception {
        // ARRANGE
        Student student = new Student("Alice", "alice@example.com");
        Student savedStudent = new Student("Alice", "alice@example.com");
        savedStudent.setId(1L);

        when(studentService.createStudent(any(Student.class))).thenReturn(savedStudent);

        // ACT & ASSERT
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    // ============================================================
    // TEST: PUT /api/students/{id} - Update a student
    // ============================================================

    @Test
    @DisplayName("PUT /api/students/1 should update and return student")
    void updateStudent_ShouldReturnUpdatedStudent_WhenExists() throws Exception {
        // ARRANGE
        Student updatedStudent = new Student("Alice Updated", "new@email.com");
        updatedStudent.setId(1L);

        when(studentService.updateStudent(eq(1L), any(Student.class)))
            .thenReturn(Optional.of(updatedStudent));

        // ACT & ASSERT
        mockMvc.perform(put("/api/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStudent)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Alice Updated"))
            .andExpect(jsonPath("$.email").value("new@email.com"));
    }

    @Test
    @DisplayName("PUT /api/students/99 should return 404 when not found")
    void updateStudent_ShouldReturn404_WhenNotExists() throws Exception {
        // ARRANGE
        Student updatedStudent = new Student("Doesn't Matter", "no@email.com");
        when(studentService.updateStudent(eq(99L), any(Student.class)))
            .thenReturn(Optional.empty());

        // ACT & ASSERT
        mockMvc.perform(put("/api/students/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStudent)))
            .andExpect(status().isNotFound());
    }

    // ============================================================
    // TEST: DELETE /api/students/{id} - Delete a student
    // ============================================================

    @Test
    @DisplayName("DELETE /api/students/1 should delete successfully")
    void deleteStudent_ShouldReturn200() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/api/students/1"))
            .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(1L);
    }
}
