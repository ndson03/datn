package com.ndson03.quanlykhoahoc.repository.course;

import com.ndson03.quanlykhoahoc.domain.entity.GradeDetails;

public interface GradeDetailsRepository {
	
	public void save(GradeDetails gradeDetails);
	
	public GradeDetails findById(int id);
	
	public void deleteById(int id);
}
