package com.ndson03.quanlykhoahoc.service.assignment;

import com.ndson03.quanlykhoahoc.domain.entity.AssignmentDetails;

import java.util.Optional;

public interface AssignmentDetailsService {

	public AssignmentDetails findByAssignmentAndStudentCourseDetailsId(int assignmentId, int studentCourseDetailsId);

	public AssignmentDetails save(AssignmentDetails assignmentDetails);

	Optional<AssignmentDetails> findById(int assignmentDetailsId);
}