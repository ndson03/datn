package com.ndson03.quanlykhoahoc.repository;

import com.ndson03.quanlykhoahoc.entity.AssignmentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentDetailsRepository extends JpaRepository<AssignmentDetails, Integer> {

    AssignmentDetails findByAssignmentIdAndStudentCourseDetailsId(int assignmentId, int studentCourseDetailsId);
}