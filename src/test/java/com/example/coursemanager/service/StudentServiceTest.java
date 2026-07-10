package com.example.coursemanager.service;

// ============================================================
// StudentServiceTest.java - Unit Tests for StudentService
// ============================================================
//
// This follows the same pattern as CourseServiceTest:
// 1. Mock the repository (fake database layer)
// 2. Inject mocks into the real service
// 3. Test each service method
//
// PATTERN: Arrange → Act → Assert
// - Arrange: Set up test data and tell mocks what to return
// - Act: Call the method we're testing
// - Assert: Check the result is correct
// ============================================================

import com.example.coursemanager.entity.Student;
import com.example.coursemanager.repository.StudentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class StudentServiceTest {

    // FAKE repository — won't touch real database
    @Mock
    private StudentRepository studentRepository;

    // REAL service — with fake repository injected
    @InjectMocks
    private StudentService studentService;

    // Test data
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        // Activate mocks
        MockitoAnnotations.openMocks(this);
        
        // Create test students
        student1 = new Student("Alice", "alice@example.com");
        student1.setId(1L);
        
        student2 = new Student("Bob", "bob@example.com");
        student2.setId(2L);
    }

    // --- Tests for getAllStudents() ---

    @Test
    @DisplayName("getAllStudents should return all students")
    void getAllStudents_ShouldReturnAllStudents() {
        // ARRANGE: fake repo returns 2 students
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        // ACT: call the service
        List<Student> result = studentService.getAllStudents();

        // ASSERT: check result
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
        assertEquals("Bob", result.get(1).getName());
        
        // VERIFY: make sure findAll() was actually called
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllStudents should return empty list when no students")
    void getAllStudents_ShouldReturnEmpty_WhenNoStudents() {
        when(studentRepository.findAll()).thenReturn(List.of());

        List<Student> result = studentService.getAllStudents();

        assertTrue(result.isEmpty());
        verify(studentRepository, times(1)).findAll();
    }

    // --- Tests for getStudentById() ---

    @Test
    @DisplayName("getStudentById should return student when exists")
    void getStudentById_ShouldReturnStudent_WhenExists() {
        // ARRANGE: ID=1 returns student1
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));

        // ACT
        Optional<Student> result = studentService.getStudentById(1L);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getName());
        assertEquals("alice@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("getStudentById should return empty when not exists")
    void getStudentById_ShouldReturnEmpty_WhenNotExists() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Student> result = studentService.getStudentById(99L);

        assertFalse(result.isPresent());
    }

    // --- Tests for createStudent() ---

    @Test
    @DisplayName("createStudent should save and return new student")
    void createStudent_ShouldSaveAndReturnStudent() {
        // ARRANGE
        when(studentRepository.save(any(Student.class))).thenReturn(student1);

        // ACT
        Student result = studentService.createStudent(student1);

        // ASSERT
        assertNotNull(result);
        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
        assertEquals(1L, result.getId());
        
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    // --- Tests for deleteStudent() ---

    @Test
    @DisplayName("deleteStudent should call repository deleteById")
    void deleteStudent_ShouldCallDelete() {
        // ARRANGE: void method, so just allow it
        doNothing().when(studentRepository).deleteById(1L);

        // ACT
        studentService.deleteStudent(1L);

        // ASSERT: verify it was called
        verify(studentRepository, times(1)).deleteById(1L);
    }

    // --- Tests for updateStudent() ---

    @Test
    @DisplayName("updateStudent should update name and email when student exists")
    void updateStudent_ShouldUpdate_WhenExists() {
        // ARRANGE
        Student updatedData = new Student("Alice Updated", "newalice@example.com");
        Student savedStudent = new Student("Alice Updated", "newalice@example.com");
        savedStudent.setId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        // ACT
        Optional<Student> result = studentService.updateStudent(1L, updatedData);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals("Alice Updated", result.get().getName());
        assertEquals("newalice@example.com", result.get().getEmail());
        verify(studentRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("updateStudent should return empty when student doesn't exist")
    void updateStudent_ShouldReturnEmpty_WhenNotExists() {
        // ARRANGE
        Student updatedData = new Student("Doesn't Matter", "no@email.com");
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Optional<Student> result = studentService.updateStudent(99L, updatedData);

        // ASSERT
        assertFalse(result.isPresent());
        verify(studentRepository, times(1)).findById(99L);
        verify(studentRepository, never()).save(any(Student.class));  // save should NOT be called
    }
}
