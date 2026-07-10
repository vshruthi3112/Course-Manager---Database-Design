package com.example.coursemanager.repository;

import com.example.coursemanager.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// Database operations for Student
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find student by email
    // Becomes: SELECT * FROM students WHERE email = ?
    Optional<Student> findByEmail(String email);

    // Check if email already exists
    // Becomes: SELECT COUNT(*) > 0 FROM students WHERE email = ?
    boolean existsByEmail(String email);
}
