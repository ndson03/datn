package com.ndson03.quanlykhoahoc.service;

import com.ndson03.quanlykhoahoc.dao.QuizOptionRepository;
import com.ndson03.quanlykhoahoc.entity.QuizOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public 	class QuizOptionServiceImpl implements QuizOptionService {

    @Autowired
    private QuizOptionRepository quizOptionRepository;

    @Override
    @Transactional
    public List<QuizOption> findByQuestionId(int questionId) {
        return quizOptionRepository.findByQuestionId(questionId);
    }

    @Override
    @Transactional
    public QuizOption findById(int id) {
        return quizOptionRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(QuizOption option) {
        quizOptionRepository.save(option);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        quizOptionRepository.deleteById(id);
    }
}