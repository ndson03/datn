package com.ndson03.quanlykhoahoc.service.quiz;

import com.ndson03.quanlykhoahoc.domain.entity.QuizOption;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuizOptionService {
    @Transactional
    List<QuizOption> findByQuestionId(int questionId);

    @Transactional
    QuizOption findById(int id);

    @Transactional
    void save(QuizOption option);

    @Transactional
    void deleteById(int id);
}
