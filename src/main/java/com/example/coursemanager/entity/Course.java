package com.example.coursemanager.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// This class = "courses" table in the database
@Entity
@Table(name = "courses")
public class Course {

    // Primary key - auto generated (1, 2, 3, 4...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Simple columns
    private String title;
    private String description;

    // ONE Course has MANY Lessons
    // mappedBy = "course" means: look at the "course" field in Lesson class
    // cascade = ALL means: if we delete a course, delete its lessons too
    // orphanRemoval = true means: if a lesson is removed from this list, delete it from the DB
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

    // MANY Courses can have MANY Students (and vice versa)
    // This creates a join table called "course_students"
    @ManyToMany
    @JoinTable(
        name = "course_students",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> enrolledStudents = new HashSet<>();

    // Empty constructor (JPA needs this)
    public Course() {}

    public Course(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // --- Helper methods to manage relationships ---

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
        lesson.setCourse(this); // link lesson back to this course
    }

    public void enrollStudent(Student student) {
        enrolledStudents.add(student);
        student.getCourses().add(this);
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Lesson> getLessons() { return lessons; }
    public void setLessons(List<Lesson> lessons) { this.lessons = lessons; }

    public Set<Student> getEnrolledStudents() { return enrolledStudents; }
    public void setEnrolledStudents(Set<Student> enrolledStudents) { this.enrolledStudents = enrolledStudents; }
}
