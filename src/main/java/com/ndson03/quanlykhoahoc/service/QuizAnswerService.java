package com.ndson03.quanlykhoahoc.service;

import com.ndson03.quanlykhoahoc.entity.QuizAnswer;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuizAnswerService {
    @Transactional
    List<QuizAnswer> findBySubmissionId(int submissionId);

    @Transactional
    void save(QuizAnswer answer);
}
