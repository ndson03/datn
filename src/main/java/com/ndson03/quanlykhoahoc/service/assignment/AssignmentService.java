package com.ndson03.quanlykhoahoc.service.assignment;

import com.ndson03.quanlykhoahoc.domain.entity.Assignment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AssignmentService {
	
	public void save(Assignment assignment);

	@Transactional
	Assignment findById(int id);

	@Transactional
	List<Assignment> findAll();

	@Transactional
	List<Assignment> findByIsQuiz(boolean isQuiz);

	public void deleteAssignmentById(int id);
}
