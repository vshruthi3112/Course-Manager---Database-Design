package com.example.coursemanager.repository;

import com.example.coursemanager.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Database operations for Lesson
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    // Find all lessons for a course, sorted by order
    // Becomes: SELECT * FROM lessons WHERE course_id = ? ORDER BY lesson_order ASC
    List<Lesson> findByCourse_IdOrderByLessonOrderAsc(Long courseId);
}
