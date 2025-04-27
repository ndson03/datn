package com.ndson03.quanlykhoahoc.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ndson03.quanlykhoahoc.entity.QuizQuestion;
import com.ndson03.quanlykhoahoc.entity.QuizOption;
import com.ndson03.quanlykhoahoc.entity.QuizSubmission;
import com.ndson03.quanlykhoahoc.entity.QuizAnswer;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
    List<QuizQuestion> findByAssignmentId(int assignmentId);
}

