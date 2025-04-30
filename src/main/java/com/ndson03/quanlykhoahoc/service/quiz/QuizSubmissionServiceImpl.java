package com.ndson03.quanlykhoahoc.service.quiz;

import com.ndson03.quanlykhoahoc.repository.quiz.QuizSubmissionRepository;
import com.ndson03.quanlykhoahoc.domain.entity.QuizSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizSubmissionServiceImpl implements QuizSubmissionService {

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Override
    @Transactional
    public QuizSubmission findByAssignmentAndStudentCourseDetailsId(int assignmentId, int studentCourseDetailsId) {
        return quizSubmissionRepository.findByAssignmentIdAndStudentCourseDetailsId(assignmentId, studentCourseDetailsId);
    }

    @Override
    @Transactional
    public List<QuizSubmission> findByAssignmentId(int assignmentId) {
        return quizSubmissionRepository.findByAssignmentId(assignmentId);
    }

    @Override
    @Transactional
    public QuizSubmission findById(int id) {
        return quizSubmissionRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(QuizSubmission submission) {
        quizSubmissionRepository.save(submission);
    }

    @Override
    public void deleteSubmissionById(int id) {
        quizSubmissionRepository.deleteById(id);
    }
}