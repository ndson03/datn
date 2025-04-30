package com.ndson03.quanlykhoahoc.domain.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lesson")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @NotBlank(message = "is required")
    @Size(min = 1, message = "is required")
    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="order_number")
    private int orderNumber;

    @ManyToOne(cascade= {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "lesson",
            fetch = FetchType.LAZY)
    private List<Content> contents;

    // Add relationship for assignments
    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "lesson",
            fetch = FetchType.LAZY)
    private List<Assignment> assignments;

    public Lesson() {
    }

    public Lesson(String title, String description, int orderNumber) {
        this.title = title;
        this.description = description;
        this.orderNumber = orderNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    // Add content to lesson
    public void addContent(Content content) {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        contents.add(content);
        content.setLesson(this);
    }

    // Remove content from lesson
    public void removeContent(Content content) {
        if (contents != null && contents.contains(content)) {
            contents.remove(content);
            content.setLesson(null);
        }
    }

    // Add assignment to lesson
    public void addAssignment(Assignment assignment) {
        if (assignments == null) {
            assignments = new ArrayList<>();
        }
        assignments.add(assignment);
        assignment.setLesson(this);
    }

    // Remove assignment from lesson
    public void removeAssignment(Assignment assignment) {
        if (assignments != null && assignments.contains(assignment)) {
            assignments.remove(assignment);
            assignment.setLesson(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lesson)) return false;

        Lesson lesson = (Lesson) o;
        return id == lesson.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}