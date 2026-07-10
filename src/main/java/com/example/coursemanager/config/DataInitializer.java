package com.example.coursemanager.config;

import com.example.coursemanager.entity.Course;
import com.example.coursemanager.entity.Lesson;
import com.example.coursemanager.entity.Student;
import com.example.coursemanager.repository.CourseRepository;
import com.example.coursemanager.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// This runs when the app starts and adds sample data to the database
@Component
public class DataInitializer implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public DataInitializer(CourseRepository courseRepository,
                          StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Only add data if database is empty
        if (courseRepository.count() > 0) {
            System.out.println("Database already has data - skipping.");
            return;
        }

        System.out.println("Adding sample data...");

        // Create students
        Student alice = studentRepository.save(new Student("Alice", "alice@example.com"));
        Student bob = studentRepository.save(new Student("Bob", "bob@example.com"));

        // Create a course with lessons
        Course javaCourse = new Course("Java Basics", "Learn Java programming");
        javaCourse.addLesson(new Lesson("Variables", "Learn about int, String...", 1));
        javaCourse.addLesson(new Lesson("Loops", "for, while, do-while", 2));
        javaCourse.addLesson(new Lesson("Classes", "OOP basics", 3));

        // Enroll students
        javaCourse.enrollStudent(alice);
        javaCourse.enrollStudent(bob);

        courseRepository.save(javaCourse);

        // Create another course
        Course webCourse = new Course("Web Development", "HTML, CSS, JS");
        webCourse.addLesson(new Lesson("HTML", "Structure of web pages", 1));
        webCourse.addLesson(new Lesson("CSS", "Styling web pages", 2));
        webCourse.enrollStudent(bob);

        courseRepository.save(webCourse);

        System.out.println("Sample data added!");
    }
}
