package com.ndson03.quanlykhoahoc.service;

import com.ndson03.quanlykhoahoc.entity.Assignment;

public interface AssignmentService {
	
	public void save(Assignment assignment);
	
	public void deleteAssignmentById(int id);
}
