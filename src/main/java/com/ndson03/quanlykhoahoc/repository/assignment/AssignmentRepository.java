package com.ndson03.quanlykhoahoc.repository.assignment;

import com.ndson03.quanlykhoahoc.domain.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByIsQuiz(boolean isQuiz);

    List<Assignment> findByLessonId(int lessonId);
}