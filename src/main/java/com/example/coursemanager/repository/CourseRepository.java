package com.example.coursemanager.repository;

import com.example.coursemanager.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// This interface gives us database operations for Course
// We get save(), findAll(), findById(), deleteById() for FREE
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Spring reads method name and creates SQL automatically!
    // This becomes: SELECT * FROM courses WHERE title LIKE '%keyword%'
    List<Course> findByTitleContainingIgnoreCase(String keyword);
}
