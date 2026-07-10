package com.example.coursemanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

// This class = "lessons" table in the database
@Entity
@Table(name = "lessons")
public class Lesson {

    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Simple columns
    private String title;
    private String content;
    private Integer lessonOrder;

    // MANY Lessons belong to ONE Course
    // @JoinColumn creates a "course_id" column in the lessons table
    // @JsonIgnore prevents infinite loop when converting to JSON
    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    // Empty constructor (JPA needs this)
    public Lesson() {}

    public Lesson(String title, String content, Integer lessonOrder) {
        this.title = title;
        this.content = content;
        this.lessonOrder = lessonOrder;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getLessonOrder() { return lessonOrder; }
    public void setLessonOrder(Integer lessonOrder) { this.lessonOrder = lessonOrder; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}
