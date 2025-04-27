package com.ndson03.quanlykhoahoc.service;

import com.ndson03.quanlykhoahoc.entity.QuizSubmission;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuizSubmissionService {
    @Transactional
    QuizSubmission findByAssignmentAndStudentCourseDetailsId(int assignmentId, int studentCourseDetailsId);

    @Transactional
    List<QuizSubmission> findByAssignmentId(int assignmentId);

    @Transactional
    QuizSubmission findById(int id);

    @Transactional
    void save(QuizSubmission submission);

    void deleteSubmissionById(int id);
}
