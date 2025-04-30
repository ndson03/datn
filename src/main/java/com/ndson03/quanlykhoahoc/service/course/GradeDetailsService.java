package com.ndson03.quanlykhoahoc.service.course;

import com.ndson03.quanlykhoahoc.domain.entity.GradeDetails;

public interface GradeDetailsService {
	
	public void save(GradeDetails gradeDetails);
	
	public GradeDetails findById(int id);
	
	public void deleteById(int id);
}
