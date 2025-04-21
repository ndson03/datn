package com.ndson03.quanlykhoahoc.dao;

import com.ndson03.quanlykhoahoc.entity.Assignment;

public interface AssignmentDao {
	
	public void save(Assignment assignment);
	
	public void deleteAssignmentById(int id);
}
