package com.ndson03.quanlykhoahoc.service.quiz;

import com.ndson03.quanlykhoahoc.repository.quiz.QuizAnswerRepository;
import com.ndson03.quanlykhoahoc.domain.entity.QuizAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizAnswerServiceImpl implements QuizAnswerService {

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Override
    @Transactional
    public List<QuizAnswer> findBySubmissionId(int submissionId) {
        return quizAnswerRepository.findByQuizSubmissionId(submissionId);
    }

    @Override
    @Transactional
    public void save(QuizAnswer answer) {
        quizAnswerRepository.save(answer);
    }
}