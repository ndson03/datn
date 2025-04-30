package com.ndson03.quanlykhoahoc.service.quiz;

import com.ndson03.quanlykhoahoc.domain.entity.QuizAnswer;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuizAnswerService {
    @Transactional
    List<QuizAnswer> findBySubmissionId(int submissionId);

    @Transactional
    void save(QuizAnswer answer);
}
