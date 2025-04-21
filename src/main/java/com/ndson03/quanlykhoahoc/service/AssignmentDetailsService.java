package com.ndson03.quanlykhoahoc.service;

import com.ndson03.quanlykhoahoc.entity.AssignmentDetails;

public interface AssignmentDetailsService {
	
	public AssignmentDetails findByAssignmentAndStudentCourseDetailsId(int assignmentId, int studentCourseDetailsId);
	
	public void save(AssignmentDetails studentCourseAssignmentDetails);
}
