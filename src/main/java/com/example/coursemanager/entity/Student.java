package com.example.coursemanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

// This class = "students" table in the database
@Entity
@Table(name = "students")
public class Student {

    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Simple columns
    private String name;
    private String email;

    // MANY Students can be in MANY Courses
    // mappedBy = "enrolledStudents" means: Course entity owns this relationship
    // @JsonIgnore prevents infinite loop in JSON
    @ManyToMany(mappedBy = "enrolledStudents")
    @JsonIgnore
    private Set<Course> courses = new HashSet<>();

    // Empty constructor (JPA needs this)
    public Student() {}

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<Course> getCourses() { return courses; }
    public void setCourses(Set<Course> courses) { this.courses = courses; }
}
