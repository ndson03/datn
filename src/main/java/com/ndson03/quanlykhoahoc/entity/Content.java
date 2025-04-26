package com.ndson03.quanlykhoahoc.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @NotBlank(message = "is required")
    @Column(name="title")
    private String title;

    @Column(name="content_type")
    private String contentType;  // TEXT, VIDEO, FILE, etc.

    @Column(name="content_data", columnDefinition = "TEXT")
    private String contentData;

    @Column(name="order_number")
    private int orderNumber;

    @ManyToOne(cascade= {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    public Content() {
    }

    public Content(String title, String contentType, String contentData, int orderNumber) {
        this.title = title;
        this.contentType = contentType;
        this.contentData = contentData;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentData() {
        return contentData;
    }

    public void setContentData(String contentData) {
        this.contentData = contentData;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Content)) return false;

        Content content = (Content) o;
        return id == content.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
