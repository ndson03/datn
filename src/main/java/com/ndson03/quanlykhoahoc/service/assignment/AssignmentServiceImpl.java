package com.ndson03.quanlykhoahoc.service.assignment;

import com.ndson03.quanlykhoahoc.repository.assignment.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndson03.quanlykhoahoc.domain.entity.Assignment;

import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {

	@Autowired
	private AssignmentRepository assignmentRepository;

	@Override
	@Transactional
	public void save(Assignment assignment) {
		assignmentRepository.save(assignment);
	}

	@Override
	@Transactional
	public Assignment findById(int id) {
		return assignmentRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public List<Assignment> findAll() {
		return assignmentRepository.findAll();
	}

	@Override
	@Transactional
	public List<Assignment> findByIsQuiz(boolean isQuiz) {
		return assignmentRepository.findByIsQuiz(isQuiz);
	}

	@Override
	@Transactional
	public void deleteAssignmentById(int id) {
		assignmentRepository.deleteById(id);
	}
}










