package com.ndson03.quanlykhoahoc.service.quiz;

import com.ndson03.quanlykhoahoc.domain.entity.QuizQuestion;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuizQuestionService {
    @Transactional
    List<QuizQuestion> findByAssignmentId(int assignmentId);

    @Transactional
    QuizQuestion findById(int id);

    @Transactional
    void save(QuizQuestion question);

    @Transactional
    void deleteById(int id);
}
