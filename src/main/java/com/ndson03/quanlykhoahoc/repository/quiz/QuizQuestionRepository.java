package com.ndson03.quanlykhoahoc.repository.quiz;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ndson03.quanlykhoahoc.domain.entity.QuizQuestion;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
    List<QuizQuestion> findByAssignmentId(int assignmentId);
}

