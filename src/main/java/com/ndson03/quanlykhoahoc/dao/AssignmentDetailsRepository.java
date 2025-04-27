package com.ndson03.quanlykhoahoc.dao;

import com.ndson03.quanlykhoahoc.entity.Assignment;
import com.ndson03.quanlykhoahoc.entity.AssignmentDetails;
import com.ndson03.quanlykhoahoc.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentDetailsRepository extends JpaRepository<AssignmentDetails, Integer> {

    AssignmentDetails findByAssignmentIdAndStudentCourseDetailsId(int assignmentId, int studentCourseDetailsId);
}