package com.ndson03.quanlykhoahoc.service.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndson03.quanlykhoahoc.repository.course.GradeDetailsRepository;
import com.ndson03.quanlykhoahoc.domain.entity.GradeDetails;

@Service
public class GradeDetailsServiceImpl implements GradeDetailsService {

	@Autowired
	private GradeDetailsRepository gradeDetailsRepository;
	
	@Override
	@Transactional
	public void save(GradeDetails gradeDetails) {
		gradeDetailsRepository.save(gradeDetails);
	}

	@Override
	@Transactional
	public GradeDetails findById(int id) {
		return gradeDetailsRepository.findById(id);
	}

	@Override
	@Transactional
	public void deleteById(int id) {
		gradeDetailsRepository.deleteById(id);
	}

}
