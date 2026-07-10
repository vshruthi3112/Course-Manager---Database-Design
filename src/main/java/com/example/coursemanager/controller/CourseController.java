package com.example.coursemanager.controller;

import com.example.coursemanager.entity.Course;
import com.example.coursemanager.entity.Lesson;
import com.example.coursemanager.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // GET /api/courses - get all courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // GET /api/courses/1 - get course by id
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/courses - create a course
    // Body: { "title": "Java", "description": "Learn Java" }
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseService.createCourse(course);
    }

    // PUT /api/courses/1 - update a course
    // Body: { "title": "Updated Title", "description": "Updated description" }
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        return courseService.updateCourse(id, course)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/courses/1 - delete a course
    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

    // GET /api/courses/search?keyword=java
    @GetMapping("/search")
    public List<Course> searchCourses(@RequestParam String keyword) {
        return courseService.searchCourses(keyword);
    }

    // POST /api/courses/1/enroll/2 - enroll student 2 in course 1
    @PostMapping("/{courseId}/enroll/{studentId}")
    public String enrollStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        return courseService.enrollStudent(courseId, studentId);
    }

    // GET /api/courses/1/lessons - get all lessons for course 1
    @GetMapping("/{courseId}/lessons")
    public List<Lesson> getCourseLessons(@PathVariable Long courseId) {
        return courseService.getLessonsForCourse(courseId);
    }

    // PUT /api/courses/1/lessons - replace all lessons for course 1
    // Body: [{"title": "Intro", "content": "Welcome!", "lessonOrder": 1}, ...]
    @PutMapping("/{courseId}/lessons")
    public ResponseEntity<Course> updateCourseLessons(
            @PathVariable Long courseId,
            @RequestBody List<Lesson> lessons) {
        return courseService.updateCourseLessons(courseId, lessons)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
