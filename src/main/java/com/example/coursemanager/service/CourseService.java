package com.example.coursemanager.service;

import com.example.coursemanager.entity.Course;
import com.example.coursemanager.entity.Lesson;
import com.example.coursemanager.entity.Student;
import com.example.coursemanager.repository.CourseRepository;
import com.example.coursemanager.repository.LessonRepository;
import com.example.coursemanager.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    // Spring injects these automatically
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;

    // Constructor dependency injection
    public CourseService(CourseRepository courseRepository,
                         StudentRepository studentRepository,
                         LessonRepository lessonRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.lessonRepository = lessonRepository;
    }

    // --- BASIC CRUD ---

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    // Update an existing course's title and description
    public Optional<Course> updateCourse(Long id, Course updatedCourse) {
        return courseRepository.findById(id).map(existing -> {
            existing.setTitle(updatedCourse.getTitle());
            existing.setDescription(updatedCourse.getDescription());
            return courseRepository.save(existing);
        });
    }

    public List<Course> searchCourses(String keyword) {
        return courseRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // --- TRANSACTION EXAMPLE ---
    // @Transactional = if anything fails, undo everything (all or nothing)
    @Transactional
    public Course createCourseWithLessons(String title, String description, List<Lesson> lessons) {
        // Step 1: Create the course
        Course course = new Course(title, description);

        // Step 2: Add each lesson to the course
        for (Lesson lesson : lessons) {
            course.addLesson(lesson);
        }

        // Step 3: Save (cascade saves the lessons too!)
        return courseRepository.save(course);
    }

    // --- ENROLLMENT ---
    @Transactional
    public String enrollStudent(Long courseId, Long studentId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        Optional<Student> studentOpt = studentRepository.findById(studentId);

        if (courseOpt.isEmpty()) return "Course not found";
        if (studentOpt.isEmpty()) return "Student not found";

        Course course = courseOpt.get();
        Student student = studentOpt.get();

        course.enrollStudent(student);
        courseRepository.save(course);

        return "Student enrolled successfully!";
    }

    // --- LESSONS ---
    public List<Lesson> getLessonsForCourse(Long courseId) {
        return lessonRepository.findByCourse_IdOrderByLessonOrderAsc(courseId);
    }

    // Replace all lessons for a given course
    // @Transactional ensures: if saving any lesson fails, none are saved
    @Transactional
    public Optional<Course> updateCourseLessons(Long courseId, List<Lesson> newLessons) {
        return courseRepository.findById(courseId).map(course -> {
            // Clear old lessons (orphanRemoval = true deletes them from DB)
            course.getLessons().clear();

            // Add each new lesson and link it to this course
            for (Lesson lesson : newLessons) {
                course.addLesson(lesson);
            }

            // Save the course (cascade saves the new lessons too)
            return courseRepository.save(course);
        });
    }
}
