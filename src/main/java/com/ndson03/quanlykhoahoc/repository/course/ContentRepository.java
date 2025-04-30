package com.ndson03.quanlykhoahoc.repository.course;

import com.ndson03.quanlykhoahoc.domain.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Integer> {

    List<Content> findByLessonIdOrderByOrderNumberAsc(int lessonId);

    @Query("SELECT MAX(c.orderNumber) FROM Content c WHERE c.lesson.id = :lessonId")
    Integer findMaxOrderNumberByLessonId(@Param("lessonId") int lessonId);
}