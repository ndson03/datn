package com.ndson03.quanlykhoahoc.repository;

import com.ndson03.quanlykhoahoc.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Integer> {
    QuizSubmission findByAssignmentIdAndStudentCourseDetailsId(int assignmentId, int studentCourseDetailsId);
    List<QuizSubmission> findByAssignmentId(int assignmentId);
}
