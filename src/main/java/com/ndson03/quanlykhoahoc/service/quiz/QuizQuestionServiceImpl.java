package com.ndson03.quanlykhoahoc.service.quiz;

import com.ndson03.quanlykhoahoc.repository.quiz.QuizQuestionRepository;
import com.ndson03.quanlykhoahoc.domain.entity.QuizQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizQuestionServiceImpl implements QuizQuestionService {

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Override
    @Transactional
    public List<QuizQuestion> findByAssignmentId(int assignmentId) {
        return quizQuestionRepository.findByAssignmentId(assignmentId);
    }

    @Override
    @Transactional
    public QuizQuestion findById(int id) {
        return quizQuestionRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(QuizQuestion question) {
        quizQuestionRepository.save(question);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        quizQuestionRepository.deleteById(id);
    }
}