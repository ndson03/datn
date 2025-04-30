package com.ndson03.quanlykhoahoc.repository;

import com.ndson03.quanlykhoahoc.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonDao extends JpaRepository<Lesson, Integer> {

    List<Lesson> findByCourseIdOrderByOrderNumberAsc(int courseId);

    @Query("SELECT MAX(l.orderNumber) FROM Lesson l WHERE l.course.id = :courseId")
    Integer findMaxOrderNumberByCourseId(@Param("courseId") int courseId);
}
